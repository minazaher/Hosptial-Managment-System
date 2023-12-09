package Table

import Model.LaboratoryResult
import slick.jdbc.MySQLProfile.api._
import java.sql.Date

class LaboratoryResultTable(tag: Tag) extends Table[LaboratoryResult](tag, "LaboratoryResults") {
  def result_id = column[Int]("result_id", O.PrimaryKey)
  def patient_id = column[Int]("patient_id")
  def test_name = column[String]("test_name")
  def test_date = column[Date]("test_date")
  def result_details = column[String]("result_details")

  override def * = (result_id, patient_id, test_name, test_date, result_details) <> (LaboratoryResult.tupled, LaboratoryResult.unapply)

  // Foreign key reference to Patients table
  def patient = foreignKey("patient_fk", patient_id, TableQuery[PatientTable])(_.patient_id)
}