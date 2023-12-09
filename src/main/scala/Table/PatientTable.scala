package Table


import Model.Patient

import java.sql.Date
import slick.jdbc.MySQLProfile.api._

class PatientTable(tag: Tag) extends Table[Patient](tag, "Patients") {
  def patient_id = column[Int]("patient_id", O.PrimaryKey)
  def patient_name = column[String]("patient_name")
  def date_of_birth = column[Date]("date_of_birth")
  def contact_info = column[String]("contact_info")

  override def * = (patient_id, patient_name, date_of_birth, contact_info) <> (Patient.tupled, Patient.unapply)
}
