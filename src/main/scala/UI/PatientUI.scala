package UI

import Actors.{AppointmentActor, LaboratoryResultActor, Login, LoginFailure, LoginSuccess, MedicalRecordsActor, PatientActor, addAppointment, getPatientMedicalRecord, insertPatient}
import DAO.{AppointmentDAO, LaboratoryResultDAO, MedicalRecordDAO, PatientDAO}
import Model.{Appointment, Patient}
import UI.MainFunction.startMainUI
import akka.actor.{ActorRef, ActorSystem, Props}
import akka.pattern.ask
import akka.util.Timeout
import config.Connection

import java.sql.{Date, Timestamp}
import java.text.SimpleDateFormat
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.DurationInt
import scala.util.{Failure, Success}

object PatientUI {

  val db = Connection.db
  val system: ActorSystem = ActorSystem("crudSystem")

  val patientDAO: PatientDAO = new PatientDAO(db)
  val appointmentDAO: AppointmentDAO = new AppointmentDAO(db)
  val labDao: LaboratoryResultDAO = new LaboratoryResultDAO(db)
  val medicalRecordDAO: MedicalRecordDAO = new MedicalRecordDAO(db)

  val labResultActor: ActorRef = system.actorOf(Props(new LaboratoryResultActor(labDao)), "labResultActor")
  val appointmentActor: ActorRef = system.actorOf(Props(new AppointmentActor(appointmentDAO)), "appointmentActor")
  val patientActor: ActorRef = system.actorOf(Props(new PatientActor(patientDAO)), "patientActor")
  val medicalRecordActor: ActorRef = system.actorOf(Props(new MedicalRecordsActor(medicalRecordDAO)), "medicalRecord")


  def hasAccount(): String = {
    println("Do you have an account? (yes/no)")
    val choice = scala.io.StdIn.readLine()
    choice match {
      case "yes" =>
        "yes"
      case "no" =>
        "no"
      case _ =>
        println("Invalid choice. Please enter yes or no.")
        hasAccount()
    }
  }

  def addPatientToDatabase(patient: Patient): Unit = {
    patientActor ! insertPatient(patient)
  }

  def getAppointmentDataFromUser(patientId: Int) : Appointment= {

    println("Enter Doctor ID:")
    val doctorId = scala.io.StdIn.readInt()

    println("Enter Month (1-12):")
    val month = scala.io.StdIn.readInt()

    println("Enter Day (1-31):")
    val date = scala.io.StdIn.readInt()

    println("Enter Hour (0-23):")
    val hour = scala.io.StdIn.readInt()

    println("Enter Minute (0-59):")
    val minute = scala.io.StdIn.readInt()

    val dateTimeStr = s"2023-$month-$date $hour:$minute:00"
    val sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
    val dateTime = sdf.parse(dateTimeStr)

    println("Enter Appointment Purpose:")
    val appointmentPurpose = scala.io.StdIn.readLine()

    Appointment(0, doctorId, patientId, new Timestamp(dateTime.getTime), appointmentPurpose, "Pending", 0)
  }

  def saveAppointment(appointment: Appointment): Unit = {
    appointmentActor ! addAppointment(appointment)
  }

  def reserveAppointment(patientId: Int): Unit = {
    val appointment = getAppointmentDataFromUser(patientId)
    saveAppointment(appointment)
  }


  def showPatientOptions(patientId: Int): Unit = {
    println("What would you like to do?")
    println("1. Reserve an Appointment")
    println("2. Request Lab Test")
    println("3. View Medical Records")

    val choice = scala.io.StdIn.readInt()
    choice match {
      case 1 =>
        reserveAppointment(patientId = patientId )
      case 2 =>
        /*

         */
      case 3 =>
        medicalRecordActor ! getPatientMedicalRecord(patientId)
      case 4 =>
        logout()
      case _ =>
        println("Invalid choice. Please enter a number between 1 and 6.")
        showPatientOptions(patientId)
    }
    showPatientOptions(patientId)
  }


  def startPatientScenario(): Unit = {
    if (hasAccount() == "yes") {
      loginUser()
    }
    else {
      signUpPatient()
      startPatientScenario()
    }
  }

  def logout(): Unit = {
    println("Logout")
    startMainUI()
  }


  def signUpPatient(): Unit = {

    println("Enter Patient Name:")
    val patientName = scala.io.StdIn.readLine()

    println("Enter Year of Birth:")
    val year = scala.io.StdIn.readInt()

    println("Enter Month of Birth (1-12):")
    val month = scala.io.StdIn.readInt()

    println("Enter Date of Birth (1-31):")
    val date = scala.io.StdIn.readInt()

    val dobStr = s"$year-$month-$date"
    val sdf = new SimpleDateFormat("yyyy-MM-dd")
    val dob = new Date(sdf.parse(dobStr).getTime)

    println("Enter Contact Info:")
    val contactInfo = scala.io.StdIn.readLine()


    addPatientToDatabase(Patient(0, patientName, dob, contactInfo))
  }

  def loginUser(): Unit = {
    println("Please Enter Your Email: ")
    val email = scala.io.StdIn.readLine()
    var id:Int = 0
    implicit val timeout: Timeout = Timeout(5.seconds) // Define a timeout

    val loginFuture = patientActor ? Login(email)

    loginFuture.onComplete{
      case Success(LoginSuccess(patientId)) =>
        println(s"User logged in with ID: $patientId")
        id = patientId
        showPatientOptions(patientId)
      case Success(LoginFailure(message)) =>
        println(s"$message")
        signUpPatient()
      case Failure(ex) =>
        println(s"Login failed due to: ${ex.getMessage}")
        system.terminate()
    }

  }

}
