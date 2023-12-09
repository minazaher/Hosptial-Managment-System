package Table


import Model.Appointment
import slick.jdbc.MySQLProfile.api._
import java.sql.Timestamp

class AppointmentTable(tag: Tag) extends Table[Appointment](tag, "Appointments") {
  def appointment_id = column[Int]("appointment_id", O.PrimaryKey)
  def doctor_id = column[Int]("doctor_id")
  def patient_id = column[Int]("patient_id")
  def appointment_date_time = column[Timestamp]("appointment_date_time")
  def appointment_purpose = column[String]("appointment_purpose")

  override def * = (appointment_id, doctor_id, patient_id, appointment_date_time, appointment_purpose) <> (Appointment.tupled, Appointment.unapply)

  // Foreign key references to Doctors and Patients tables
  def doctor = foreignKey("doctor_fk", doctor_id, TableQuery[DoctorTable])(_.doctor_id)
  def patient = foreignKey("patient_fk", patient_id, TableQuery[PatientTable])(_.patient_id)
}

