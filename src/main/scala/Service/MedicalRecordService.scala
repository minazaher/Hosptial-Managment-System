package Service

import DAO.MedicalRecordDAO
import Model.MedicalRecord
import slick.jdbc.MySQLProfile.api._

import scala.concurrent.Future

class MedicalRecordService(medicalRecordDAO: MedicalRecordDAO) {
  def getMedicalRecordById(id: Int): Future[Option[MedicalRecord]] = {
    medicalRecordDAO.getMedicalRecordById(id)
  }

  def addMedicalRecord(record: MedicalRecord): Future[Int] = {
    medicalRecordDAO.addMedicalRecord(record)
  }

  def getPatientMedicalRecord(patientId: Int): Future[Option[MedicalRecord]] = {
    medicalRecordDAO.getMedicalRecordForPatient(patientId)
  }
}
