import scala.annotation.tailrec
import Actors._
import DAO._
import config.Connection
import akka.actor.{ActorRef, ActorSystem, Props}
import Model.{Appointment, Doctor, MedicalRecord, Patient, Staff}

object  DoctorUI {
    val db = Connection.db
    val system: ActorSystem = ActorSystem("crudSystem")
    val doctorDAO = new DoctorDAO(db)
    val doctorActor: ActorRef = system.actorOf(Props(new DoctorActor(doctorDAO)), "doctorActor")
    def loginUser(): Int = {
        println("Enter your email: ")
        val email = scala.io.StdIn.readLine()
        // println("Enter your password: ")
        // val password = scala.io.StdIn.readLine()
        doctorActor ! login(email)
        // get user id from actor
        
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

            showDoctorOptions();
        }
        else {
            signUpDoctor()
            startDoctorScenario()
        }
    }
    def showDoctorOptions(doctor_id: Int): Unit = {
        println("Select an option: ")
        println("1. View Appointments")
        println("2. Logout")
        val choice = scala.io.StdIn.readInt()
        choice match {
            case 1 =>
                viewAppointments(doctorId = doctor_id)
            case 2 =>
                logout()
            case _ =>
                println("Invalid choice. Please enter a number between 1 and 6.")
                showDoctorOptions()
        }
    }
    def viewAppointments(doctorId: Int): Unit = {
        println("View Appointments")
        doctorActor ! GetAppointments(doctorId)
    }

    def logout(): Unit = {
        println("Logout")
        startDoctorScenario()
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
}