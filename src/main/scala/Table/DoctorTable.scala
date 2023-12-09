package Table
import Model.Doctor
import slick.jdbc.MySQLProfile.api._

class DoctorTable(tag: Tag) extends Table[Doctor](tag, "Doctors") {
  def doctor_id = column[Int]("doctor_id", O.PrimaryKey)
  def doctor_name = column[String]("doctor_name")
  def specialization = column[String]("specialization")
  def contact_info = column[String]("contact_info")

  override def * = (doctor_id, doctor_name, specialization, contact_info) <> (Doctor.tupled, Doctor.unapply)
}