package Actors

import DAO.DoctorDAO
import Model.Doctor
import _root_.Table.DoctorTable
import akka.actor.{Actor, ActorLogging}
import config.Connection
import slick.jdbc.MySQLProfile.api._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.util.{Failure, Success}

case class CreateDoctor(doctor: Doctor)
case class GetDoctorById(id: Int)
case class InitializeDoctorDAO(dao: DoctorDAO)

case class Login(email: String)
case class LoginSuccess(userId: Int)
case class LoginFailure(message: String)
case class GetAppointments(doctorId: Int)


class DoctorActor(dao: DoctorDAO) extends Actor with ActorLogging {

  override def receive: Receive = {
    case CreateDoctor(doctor) =>
      dao.doesDoctorExist(doctor).flatMap { exists =>
        if (!exists) {
          dao.addDoctor(doctor).map { _ =>
            println("Account Added")
          }.recover {
            case ex => println(s"Query Failed Because of: $ex")
          }
        } else {
          Future.successful(println("Doctor already exists"))
        }
      }
    case Login(email) =>
      val originalSender = sender()

      val loginResult = dao.login(email)
      loginResult.onComplete {
        case Success(Some(userId)) =>
//          log.info(s"User with email $email logged in. User ID: $userId")
          originalSender ! userId

        case Success(None) =>
//          log.info(s"User with email $email doesn't exist.")
          originalSender ! LoginFailure("Account does not exist.")

        case Failure(ex) =>
//          log.error(s"Login failed for email $email. Reason: ${ex.getMessage}")
          originalSender ! LoginFailure("Login failed.")
      }
    case GetAppointments(doctorId) =>
      val originalSender = sender()
      val appointments = dao.getAppointments(doctorId)
      appointments.onComplete {
        case Success(appointments) =>
          originalSender ! appointments
        case Failure(ex) =>
          originalSender ! s"Failed to get appointments. Reason: ${ex.getMessage}"
      }

  }
}