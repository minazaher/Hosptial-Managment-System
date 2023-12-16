package Table

import Model.MedicalRecord
import slick.jdbc.MySQLProfile.api._
import slick.lifted.Tag

class MedicalRecordTable(tag: Tag) extends Table[MedicalRecord](tag, "MedicalRecords") {
  def record_id = column[Int]("record_id", O.PrimaryKey, O.AutoInc)
  def patient_id = column[Int]("patient_id")
  def vital_signs = column[String]("vital_signs")
  def medical_history = column[String]("medical_history")
  def medication_details = column[String]("medication_details")

  override def * = (record_id, patient_id, vital_signs, medical_history, medication_details) <> (MedicalRecord.tupled, MedicalRecord.unapply)

  // Foreign key reference to Patients table
  def patient = foreignKey("patient_fk", patient_id, TableQuery[PatientTable])(_.patient_id)
}