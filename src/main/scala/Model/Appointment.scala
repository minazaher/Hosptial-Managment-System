package Model

import java.sql.Timestamp

case class Appointment(appointment_id: Int, doctor_id: Int, patient_id: Int, appointment_date_time: Timestamp, appointment_purpose: String)
