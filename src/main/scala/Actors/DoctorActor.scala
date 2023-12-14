package Actors

import DAO.DoctorDAO
import Model.Doctor
import _root_.Table.DoctorTable
import akka.actor.{Actor, ActorLogging}
import config.Connection
import slick.jdbc.MySQLProfile.api._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.{Failure, Success}

case class CreateDoctor(doctor: Doctor)
case class GetDoctorById(id: Int)
case class InitializeDoctorDAO(dao: DoctorDAO)


class DoctorActor(dao: DoctorDAO) extends Actor with ActorLogging {

  override def receive: Receive = {
    case CreateDoctor(doctor) =>
      dao.addDoctor(doctor).onComplete {
        case Success(doc) => println("Account Added")
        case Failure(ex) => println(s"Query Failed Because of : $ex")
      }
  }
}