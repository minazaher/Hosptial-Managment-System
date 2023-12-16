package UI

import Actors.{AppointmentActor, CreateDoctor, DoctorActor, GetAppointments, Login, LoginFailure, LoginSuccess, StaffActor, addMedicalRecord, approveAppointment, getAppointmentByStatus, getPatientMedicalRecord, insertStaffMember, onAppointmentsRetrieved, staffLogin}
import DAO.{AppointmentDAO, DoctorDAO, StaffDAO}
import Model.{Doctor, MedicalRecord, Staff}
import UI.MainFunction.startMainUI
import akka.actor.{ActorRef, ActorSystem, Props}
import akka.pattern.ask
import akka.util.Timeout
import config.Connection

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.DurationInt
import scala.util.{Failure, Success}

object StaffUI {
  val db = Connection.db
  val system: ActorSystem = ActorSystem("Hospital System")

  val staffDAO = new StaffDAO(db)
  val appointmentDAO = new AppointmentDAO(db)

  val staffActor: ActorRef = system.actorOf(Props(new StaffActor(staffDAO)), "staffActor")
  val appointmentActor: ActorRef = system.actorOf(Props(new AppointmentActor(appointmentDAO)), "appointmentActor")

  def loginUser(): Int = {
    println("Please Enter Your Email: ")
    val email = scala.io.StdIn.readLine()
    var id:Int = 0
    implicit val timeout: Timeout = Timeout(5.seconds) // Define a timeout

    val loginFuture = staffActor ? staffLogin(email)

    loginFuture.onComplete{
      case Success(LoginSuccess(memberId)) =>
        println(s"User logged in with ID: $memberId")
        id = memberId
        showStaffOptions(id)
      case Success(LoginFailure(message)) =>
        println(s"$message")
        signUpStaff()
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

  def startStaffScenario(): Unit = {
    if (hasAccount() == "yes") {
      loginUser()
    }
    else {
      signUpStaff()
      startStaffScenario()
    }
  }

  def viewAllAppointments(staff_Id:Int) = {

    implicit val timeout: Timeout = Timeout(5.seconds) // Define a timeout

    val result = appointmentActor ? getAppointmentByStatus("Pending")

    result.onComplete{
      case Success(onAppointmentsRetrieved(appointments)) =>
        if (appointments.isEmpty) {
          println("No Appointments Found")
          showStaffOptions(staff_Id)
        }
        else {
          println(s"Pending Appointments  are : ")
          appointments.foreach(appointment => println(appointment.toString))
          selectAppointmentsToApproved(staff_Id)
        }
      case Failure(ex) =>
        println(s"error due to : $ex" )
        showStaffOptions(staff_Id)
    }

  }


  def selectAppointmentsToApproved(staffId: Int) = {
    println("Choose Appointment to Approve ")
    val choice:Int = scala.io.StdIn.readInt()
    appointmentActor ! approveAppointment(choice, staffId)
    Thread.sleep(1500)
    showStaffOptions(staffId)

  }

  def showStaffOptions(staff_Id: Int): Unit = {
    println("Select an option: ")
    println("1. View Appointments")
    println("2. Logout")
    val choice = scala.io.StdIn.readInt()
    choice match {
      case 1 =>
        viewAllAppointments(staff_Id)
      case 2 =>
        logout()
      case _ =>
        println("Invalid choice. Please enter a number between 1 and 2.")
        showStaffOptions(staff_Id)
    }
  }

  def logout(): Unit = {
    println("Logout")
    startMainUI()
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


  def addStaffMemberToDatabase(member: Staff): Unit = {
    staffActor ! insertStaffMember(member)
  }


}
