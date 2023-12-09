package DAO


import Model.MedicalRecord
import Table.MedicalRecordTable
import slick.jdbc.MySQLProfile.api._

import scala.concurrent.Future

class MedicalRecordDAO(val db: Database) {
  val medicalRecords = TableQuery[MedicalRecordTable]

  def getMedicalRecordById(id: Int): Future[Option[MedicalRecord]] = {
    db.run(medicalRecords.filter(_.record_id === id).result.headOption)
  }

  def addMedicalRecord(record: MedicalRecord): Future[Int] = {
    db.run(medicalRecords += record)
  }

  def getMedicalRecordForPatient(patientId: Int): Future[Option[MedicalRecord]]= {
    db.run(medicalRecords.filter(_.patient_id === patientId).result.headOption)
  }
}
