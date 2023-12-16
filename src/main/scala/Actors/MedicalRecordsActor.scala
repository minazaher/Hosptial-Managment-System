package Actors

import DAO.MedicalRecordDAO
import Model.MedicalRecord
import akka.actor.{Actor, ActorLogging}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.{Failure, Success}


case class getMedicalRecordById(id: Int)
case class addMedicalRecord(record: MedicalRecord)
case class getPatientMedicalRecord(patientId: Int)

case class onMedicalRecordsRetrieved(medicalRecords: Seq[MedicalRecord])

class MedicalRecordsActor (dao: MedicalRecordDAO) extends Actor  {


  override def receive: Receive = {
    case addMedicalRecord(record) =>
      dao.addMedicalRecord(record).onComplete {
        case Success(_) => println("Record Added")
        case Failure(ex) => println(s"Query Failed Because of : $ex")
      }
    case getPatientMedicalRecord(patientId) =>
      val originalSender = sender()
      dao.getMedicalRecordForPatient(patientId).onComplete {
      case Success(recordOpt) =>
        originalSender ! onMedicalRecordsRetrieved(recordOpt)
      case Failure(ex) => println(s"Query Failed Because of : $ex")
    }

  }


}
