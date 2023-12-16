import Actors._
import DAO._
import Model.{Appointment, Doctor, MedicalRecord, Patient, Staff}
import akka.actor.{ActorRef, ActorSystem, Props}
import akka.pattern.ask
import akka.util.Timeout
import config.Connection

import java.sql.{Date, Timestamp}
import java.text.SimpleDateFormat
import scala.annotation.tailrec
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.DurationInt
import scala.util.{Failure, Success}

object Main {
  val db = Connection.db


  // Create ActorSystem and Actors
  val system: ActorSystem = ActorSystem("crudSystem")
  val doctorDAO = new DoctorDAO(db)
  val patientDAO: PatientDAO = new PatientDAO(db)
  val medicalRecordDao: MedicalRecordDAO = new MedicalRecordDAO(db)
  val appointmentDAO: AppointmentDAO = new AppointmentDAO(db)
  val labDao: LaboratoryResultDAO = new LaboratoryResultDAO(db)
  val staffDao: StaffDAO = new StaffDAO(db)

  val doctorActor: ActorRef = system.actorOf(Props(new DoctorActor(doctorDAO)), "doctorActor")
  val patientActor: ActorRef = system.actorOf(Props(new PatientActor(patientDAO)), "patientActor")
  val medicalRecordActor: ActorRef = system.actorOf(Props(new MedicalRecordsActor(medicalRecordDao)), "medicalRecordActor")
  val appointmentActor: ActorRef = system.actorOf(Props(new AppointmentActor(appointmentDAO)), "appointmentActor")
  val labResultActor: ActorRef = system.actorOf(Props(new LaboratoryResultActor(labDao)), "labResultActor")
  val staffActor: ActorRef = system.actorOf(Props(new StaffActor(staffDao)), "staffActor")


  def main(args: Array[String]): Unit = {

    println("Welcome to the Hospital Management System")

    println("Select User Type? (Enter 'doctor' or 'patient' or 'staff member'")
    val userType = scala.io.StdIn.readLine().toLowerCase.trim

    userType match {
      case "doctor" =>
        startDoctorScenario()
      case "patient" =>
        startPatientScenario()
      case "staff member" =>
        startStaffScenario()
      case _ =>
        println("Invalid user type. Please enter 'doctor' or 'patient'.")
    }
  }



  def selectAppointmentsToApproved() = {
    println("Choose Appointment to Approve ")
    val choice:Int = scala.io.StdIn.readInt()
   }

  def signUpStaff(): Unit = {
    println("Please Enter your name: ")
    val name = scala.io.StdIn.readLine()

    println("Please Enter your role: ")
    val role = scala.io.StdIn.readLine()

    println("Please Enter your email: ")
    val email = scala.io.StdIn.readLine()
    val member: Staff = Staff(0, name,role, email)

    staffActor ! insertStaffMember(member)
  }

  def startStaffScenario(): Unit = {
    val hasAccount = askIfHasAnAccount()
    if (hasAccount == "yes") {
      login()
    }
    else{
      signUpStaff()
    }
  }
  def login(): Unit ={
    println("Please Enter Your Email: ")
    val email = scala.io.StdIn.readLine()

    implicit val timeout: Timeout = Timeout(5.seconds) // Define a timeout

    val loginFuture = staffActor ? staffLogin(email)

    loginFuture.onComplete{
      case Success(LoginSuccess(staffId)) =>
        println(s"User logged in with ID: $staffId")
        showAppointmentsList()
        val appointmentId = scala.io.StdIn.readInt()
        appointmentActor ! approveAppointment(appointmentId, staffId)

      case Success(LoginFailure(message)) =>
        println(s"$message")
        system.terminate()

      case Failure(ex) =>
        println(s"Login failed due to: ${ex.getMessage}")
        system.terminate()
    }
  }


  def showAppointmentsList(): Unit={
    appointmentActor ! getAppointmentByStatus("Pending")
  }


  @tailrec
  def startPatientScenario(): Unit ={
    val hasAccount = askIfHasAnAccount()
    if (hasAccount == "yes") {
//      val patientId = getUserId;
      showPatientOptions()
      val option = scala.io.StdIn.readInt()
//      handlePatientOptionChoice(option, patientId)
    }
    else{
      signUpPatient()
      startPatientScenario()
    }
  }

  def addPatientToDatabase(patient: Patient): Unit = {
    patientActor ! insertPatient(patient)
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


  def showPatientOptions(): Unit = {
    println("What would you like to do?")
    println("1. Reserve an Appointment")
    println("2. Request Lab Test")
    println("3. View Medical Records")
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

  def handlePatientOptionChoice(option: Int, patientId: Int): Unit = {
    option match {
      case 1 =>
        println("Reserving appointment...")
        reserveAppointment(patientId)
      case 2 =>
        println("Getting Laboratory Results...")
        labResultActor ! getLaboratoryResultsForPatient(patientId)
      case 3 =>
        println("Fetching your medical records...")
        medicalRecordActor ! getPatientMedicalRecord(patientId)
      case _ =>
        println("Invalid option.")
    }

  }


  @tailrec
  def startDoctorScenario(): Unit = {
    val hasAccount = askIfHasAnAccount()
    if (hasAccount == "yes") {
      loginUser()
//      showDoctorOptions();
    }
    else {
      signUpDoctor()
      startDoctorScenario()
    }

  }

  def signUpDoctor(): Unit = {
    println("Enter Your Information:")

    println("Name :")
    val name = scala.io.StdIn.readLine()

    println("Specialization:")
    val spec = scala.io.StdIn.readLine()

    println("Email:")
    val email = scala.io.StdIn.readLine()
    val doctor: Doctor = Doctor(doctor_id = 5, doctor_name = name, specialization = spec, contact_info = email)
    addDoctorToDatabase(doctor)
  }

  def addDoctorToDatabase(doctor: Doctor): Unit = {
    doctorActor ! CreateDoctor(doctor)
  }

  def askIfHasAnAccount(): String = {
    println("Do you have an account? (yes/no)")
    scala.io.StdIn.readLine().toLowerCase.trim
  }



  def loginUser(): Unit = {
    println("Please Enter you Email : ")
    val email  = scala.io.StdIn.readLine()
    implicit val timeout: Timeout = Timeout(5.seconds) // Define a timeout


    val loginFuture = doctorActor ? Login(email)


    loginFuture.onComplete {
      case Success(LoginSuccess(userId)) =>
        // Action upon successful login
        println(s"User logged in with ID: $userId")
        showDoctorOptions()
        val option = scala.io.StdIn.readInt()
        handleDoctorOptionChoice(option, userId);

      case Success(LoginFailure(message)) =>
        println(s"Login failed: $message")
        system.terminate()

      case Failure(ex) =>
        // Handle any other failure during login attempt
        println(s"Login failed due to: ${ex.getMessage}")
        system.terminate()
    }
  }

  def showDoctorOptions(): Unit = {
    println("What would you like to do?")
    println("1. View Appointments")
    println("2. View Medical Record for Specific Patient")
    println("3. Add Medical Records")
  }

  def handleDoctorOptionChoice(option: Int, doctorId: Int): Unit = {
    option match {
      case 1 =>
        println("Fetching appointments...")
        fetchAppointmentsForDoctorFromDatabase(doctorId)
      case 2 =>
        println("Please Write Down Patient ID : ")
        val patientId = scala.io.StdIn.readInt()
        println("Fetching patients' medical records...")
        fetchMedicalRecordDataFromDatabase(patientId)
      case 3 =>
        println("Please Write Down Patient ID : ")
        val patientId = scala.io.StdIn.readInt()
        println("Updating medical records...")
        addMedicalRecordForPatient(patientId)
      case _ =>
        println("Invalid option.")
    }
  }

  def fetchMedicalRecordDataFromDatabase(patientId: Int): Unit = {
    medicalRecordActor ! getPatientMedicalRecord(patientId)
  }

  def fetchAppointmentsForDoctorFromDatabase(doctorId: Int): Unit = {
      appointmentActor ! getAppointmentsForDoctor(doctorId)
  }

  def addMedicalRecordForPatient(patientId: Int): Unit = {
    medicalRecordActor ! addMedicalRecord(getMedicalRecordDataFromUser(patientId))
  }

  def getMedicalRecordDataFromUser(patientId: Int): MedicalRecord = {
    println("Enter Medical Record Information:")

    println("Vital Signs:")
    val vitalSigns = scala.io.StdIn.readLine()

    println("Medical History:")
    val medicalHistory = scala.io.StdIn.readLine()

    println("Medication Details:")
    val medicationDetails = scala.io.StdIn.readLine()

    MedicalRecord(0, patientId, vitalSigns, medicalHistory, medicationDetails)
  }

}
