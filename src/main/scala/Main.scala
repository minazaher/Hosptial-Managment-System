import DAO.{AppointmentDAO, DoctorDAO, LaboratoryResultDAO, MedicalRecordDAO, PatientDAO}
import Model.{Doctor, MedicalRecord}
import Service._
import config.Connection

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.util.{Failure, Success, Try}
import java.sql.Date
import java.sql.Timestamp
import scala.concurrent.Await
import scala.concurrent.duration.Duration
import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.{Failure, Success}

object Main
{
  val db = Connection.db;


  // Initialize services for different entities
  val doctorService = new DoctorService(new DoctorDAO(db))
  val patientService = new PatientService(new PatientDAO(db))
  val appointmentService = new AppointmentService(new AppointmentDAO(db))
  val laboratoryResultService = new LaboratoryResultService(new LaboratoryResultDAO(db))
  val medicalRecordService = new MedicalRecordService(new MedicalRecordDAO(db))


  def main(args: Array[String]): Unit ={

    println("Welcome to the Hospital Management System")

    println("Are you a Doctor or a Patient? (Enter 'doctor' or 'patient')")
    val userType = scala.io.StdIn.readLine().toLowerCase.trim

    userType match {
      case "doctor" =>
        startDoctorScenario()
      case "patient" =>
        // Patient scenario
        println("Do you have an account? (yes/no)")
        val hasAccount = scala.io.StdIn.readLine().toLowerCase.trim

        if (hasAccount == "yes") {
          // Options for registered patient
          println("What would you like to do?")
          println("1. Reserve an Appointment")
          println("2. Request Lab Test")
          println("3. View Medical Records")
          // Other options for patients...

          val option = scala.io.StdIn.readInt()
          option match {
            case 1 =>
              // Reserve an appointment
              println("Reserving appointment...")
            // patientService.reserveAppointment(patientId)
            case 2 =>
              // Request lab test
              println("Requesting lab test...")
            // patientService.requestLabTest(patientId)
            case 3 =>
              // View own medical records
              println("Fetching your medical records...")
            // patientService.viewOwnMedicalRecords(patientId)
            case _ =>
              println("Invalid option.")
          }
        } else {
          // Patient without an account scenario
          println("Please register an account to avail of our services.")
        }

      case _ =>
        println("Invalid user type. Please enter 'doctor' or 'patient'.")
    }
  }




  def startDoctorScenario(): Unit ={
    val hasAccount = askIfHasAnAccount();
    if (hasAccount == "yes"){
      val doctorId = getDoctorId;
      showDoctorOptions();
      val option = scala.io.StdIn.readInt()
      handleOptionChoice(option, doctorId);
    }
    else {
      println("Please contact the administration to create an account.")
    }

  }



  def askIfHasAnAccount(): String ={
    println("Do you have an account? (yes/no)")
    scala.io.StdIn.readLine().toLowerCase.trim
  }
  def getDoctorId: Int ={
    println("Please Enter you ID : ")
    scala.io.StdIn.readInt()
  }


  def showDoctorOptions(): Unit ={
    println("What would you like to do?")
    println("1. View Appointments")
    println("2. View Medical Record for Specific Patient")
    println("3. Add Medical Records")
  }

  def handleOptionChoice(option:Int, doctorId: Int): Unit ={
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

  def fetchMedicalRecordDataFromDatabase(patientId : Int): Unit ={
    medicalRecordService.getPatientMedicalRecord(patientId).onComplete{
      case Success(recordOpt) =>
        recordOpt.foreach { record =>
          val formattedRecord = record.formattedString
          println(s"The Needed Medical Record for Patient with ID : ($patientId), is:\n$formattedRecord")
        }
      case Failure(ex) => println(s"Query Failed Because of : $ex")
    }
    Thread.sleep(5000)
  }


  def fetchAppointmentsForDoctorFromDatabase(doctorId: Int): Unit = {
    appointmentService.getAppointmentsForDoctor(doctorId).onComplete {
      case Success(appointments) =>
        appointments match {
          case Nil =>
            println("No appointments found for this doctor.")
          case _ =>
            println(s"Appointments for doctor with ID : $doctorId:")
            appointments.foreach(appointment => println(appointment.toString))
        }
      case Failure(ex) =>
        println(s"Query failed because of: $ex")
    }
    Thread.sleep(5000)
  }



  def addMedicalRecordForPatient(patientId: Int): Unit ={
    medicalRecordService.addMedicalRecord(getMedicalRecordDataFromUser(patientId)).onComplete{
      case Success(record) => println(s"record added for patient with ID: $patientId")
      case Failure(ex) => println(s"Query Failed Because of : $ex")
    }
    Thread.sleep(5000)
  }

  def getMedicalRecordDataFromUser(patientId: Int): MedicalRecord ={
    println("Enter Medical Record Information:")

    println("Vital Signs:")
    val vitalSigns = scala.io.StdIn.readLine()

    println("Medical History:")
    val medicalHistory = scala.io.StdIn.readLine()

    println("Medication Details:")
    val medicationDetails = scala.io.StdIn.readLine()

    new MedicalRecord(0, patientId, vitalSigns, medicalHistory,medicationDetails)
  }

}
