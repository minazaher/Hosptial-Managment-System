package Actors

import DAO.LaboratoryResultDAO
import akka.actor.{Actor, ActorLogging}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.{Failure, Success}

case class getLaboratoryResultsForPatient(patientId: Int)

class LaboratoryResultActor(dao: LaboratoryResultDAO) extends Actor  {

  override def receive: Receive = {
    case getLaboratoryResultsForPatient(id) =>
      dao.getLaboratoryResultsForPatient(id).onComplete {
        case Success(_) => println("Account Added")
        case Failure(ex) => println(s"Query Failed Because of : $ex")
      }

  }

}
