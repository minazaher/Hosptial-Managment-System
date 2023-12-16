package Actors
import DAO.PatientDAO
import Model.Patient
import Table.PatientTable
import akka.actor.{Actor, ActorLogging}
import slick.lifted.TableQuery

import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.{Failure, Success}


case class getPatientById(id: Int)
case class insertPatient(patient: Patient)
case class loginPatient(email: String)


class PatientActor(patientDAO: PatientDAO) extends Actor with ActorLogging {
  val doctorsTable = TableQuery[PatientTable]

  override def receive: Receive = {
    case insertPatient(patient) =>
      patientDAO.insertPatient(patient ).onComplete {
        case Success(patient) => println(s"Account Added For patient ${patient}")
        case Failure(ex) => println(s"Query Failed Because of : $ex")
      }
    case getPatientById(id) =>
      patientDAO.getPatientById(id).onComplete{
        case Success(patient) => println(s"Account data For patient ${patient}")
        case Failure(ex) => println(s"Query Failed Because of : $ex")
      }
    case Login(email) =>
      val originalSender = sender()

      val loginResult = patientDAO.login(email)
      loginResult.onComplete {
        case Success(Some(userId)) =>
          originalSender ! LoginSuccess(userId)

        case Success(None) =>
          originalSender ! LoginFailure("Account does not exist.")

        case Failure(ex) =>
          originalSender ! LoginFailure("Login failed.")
      }
  }
}

