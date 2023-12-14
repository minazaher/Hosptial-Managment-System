package Actors

import DAO.MedicalRecordDAO
import Model.MedicalRecord
import akka.actor.{Actor, ActorLogging}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.{Failure, Success}


case class getMedicalRecordById(id: Int)
case class addMedicalRecord(record: MedicalRecord)
case class getPatientMedicalRecord(patientId: Int)

class MedicalRecordsActor (dao: MedicalRecordDAO) extends Actor  {


  override def receive: Receive = {
    case addMedicalRecord(record) =>
      dao.addMedicalRecord(record).onComplete {
        case Success(record) => println("Account Added")
        case Failure(ex) => println(s"Query Failed Because of : $ex")
      }
    case getPatientMedicalRecord(patientId) =>
      dao.getMedicalRecordForPatient(patientId).onComplete {
      case Success(recordOpt) =>
        recordOpt.foreach { record =>
          val formattedRecord = record.formattedString
          println(s"The Needed Medical Record for Patient with ID : ($patientId), is:\n$formattedRecord")
        }
      case Failure(ex) => println(s"Query Failed Because of : $ex")
    }

  }


}
