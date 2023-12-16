package UI

import Actors._
import DAO._
import Model.{Doctor, MedicalRecord}
import UI.MainFunction.startMainUI
import akka.actor.{ActorRef, ActorSystem, Props}
import akka.pattern.ask
import akka.util.Timeout
import config.Connection

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.DurationInt
import scala.util.{Failure, Success}

object DoctorUI {
    val db = Connection.db
    val system: ActorSystem = ActorSystem("Hospital System")

    val doctorDAO = new DoctorDAO(db)
    val medicalRecordDao: MedicalRecordDAO = new MedicalRecordDAO(db)

    val doctorActor: ActorRef = system.actorOf(Props(new DoctorActor(doctorDAO)), "doctorActor")
    val medicalRecordActor: ActorRef = system.actorOf(Props(new MedicalRecordsActor(medicalRecordDao)), "medicalRecordActor")


    def loginUser(): Int = {
        println("Please Enter Your Email: ")
        val email = scala.io.StdIn.readLine()
        var id:Int = 0
        implicit val timeout: Timeout = Timeout(5.seconds) // Define a timeout

        val loginFuture = doctorActor ? Login(email)

        loginFuture.onComplete{
            case Success(LoginSuccess(doctorId)) =>
                println(s"User logged in with ID: $doctorId")
                id = doctorId
                showDoctorOptions(id)
            case Success(LoginFailure(message)) =>
                println(s"$message")
                signUpDoctor()
            case Failure(ex) =>
                println(s"Login failed due to: ${ex.getMessage}")
                system.terminate()
        }
        id
    }
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

    def startDoctorScenario(): Unit = {
        if (hasAccount() == "yes") {
            loginUser()
        }
        else {
            signUpDoctor()
            startDoctorScenario()
        }
    }
    def showDoctorOptions(doctor_id: Int): Unit = {
        println("Select an option: ")
        println("1. View Appointments")
        println("2. Get Patient Medical Record")
        println("3. Add Patient Medical Record")
        println("4. Logout")
        val choice = scala.io.StdIn.readInt()
        choice match {
            case 1 =>
                viewAppointments(doctorId = doctor_id)
            case 2 =>
                fetchPatientMedicalRecord(doctorId = doctor_id)
            case 3 =>
                addMedicalRecordForPatient(doctor_id)
            case 4 =>
                logout()
            case _ =>
                println("Invalid choice. Please enter a number between 1 and 6.")
                showDoctorOptions(doctor_id)
        }
    }


    def logout(): Unit = {
        println("Logout")
        startMainUI()
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
        loginUser()
    }

    def viewAppointments(doctorId: Int): Unit = {
        implicit val timeout: Timeout = Timeout(5.seconds) // Define a timeout

        val result = doctorActor ? GetAppointments(doctorId)

        result.onComplete{
            case Success(onAppointmentsRetrieved(appointments)) =>
                appointments.foreach(appointment => println(appointment.toString))
                showDoctorOptions(doctorId)
            case Failure(exception) => println(s"error due to $exception")
        }
    }

    def addDoctorToDatabase(doctor: Doctor): Unit = {
        doctorActor ! CreateDoctor(doctor)
    }

    def fetchPatientMedicalRecord(doctorId: Int): Unit = {
        println("Enter Patient Id: ")
        val patientId = scala.io.StdIn.readInt()
        implicit val timeout: Timeout = Timeout(5.seconds) // Define a timeout

        val result =  medicalRecordActor ? getPatientMedicalRecord(patientId)
        result.onComplete{
            case Success(onMedicalRecordsRetrieved(records)) =>
                records.foreach { record =>
                    val formattedRecord = record.formattedString
                    println(s"The Needed Medical Record for Patient with ID : ($patientId), is:\n$formattedRecord")
                }
            showDoctorOptions(doctorId)
            case Failure(exception) => println(s"Query failed due to : $exception" )
        }
    }

    def addMedicalRecordForPatient(doctorId: Int): Unit = {
        medicalRecordActor ! addMedicalRecord(getMedicalRecordDataFromUser())
        Thread.sleep(1500)
        showDoctorOptions(doctor_id = doctorId )
    }

    def getMedicalRecordDataFromUser(): MedicalRecord = {
        println("Enter Patient Id: ")
        val patientId = scala.io.StdIn.readInt()

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