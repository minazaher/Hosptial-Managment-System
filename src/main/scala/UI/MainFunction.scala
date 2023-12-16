package UI

import akka.actor.ActorSystem

object MainFunction {

  def startMainUI(): Unit = {
    val system: ActorSystem = ActorSystem("Hospital System")

    println("Welcome to the Hospital Management System")

    println("Select User Type? (Enter 'doctor' or 'patient' or 'staff member') or 'Exit' to terminate")
    val userType = scala.io.StdIn.readLine().toLowerCase.trim

    userType match {
      case "doctor" =>
        DoctorUI.startDoctorScenario()
      case "patient" =>
        PatientUI.startPatientScenario()
      case "staff member" =>
        StaffUI.startStaffScenario()
      case "exit" =>
        system.terminate()
      case _ =>
        println("Invalid user type. Please enter 'doctor' or 'patient'.")
    }
  }


}
