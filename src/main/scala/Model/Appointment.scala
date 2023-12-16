package Model

import java.sql.Timestamp
import java.text.SimpleDateFormat

case class Appointment(appointment_id: Int, doctor_id: Int, patient_id: Int, appointment_date_time: Timestamp, appointment_purpose: String, status: String, approved_by: Int){
  private val dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")

  override def toString: String = {
    val formattedDateTime = dateFormat.format(appointment_date_time)
    s"Appointment ID: $appointment_id, " +
      s"Doctor ID: $doctor_id, " +
      s"Patient ID: $patient_id, " +
      s"Appointment Date: $formattedDateTime, " +
      s"Purpose: $appointment_purpose, "+
      s"Status : $status, "+
      s"Approved By: $approved_by"
  }
}
