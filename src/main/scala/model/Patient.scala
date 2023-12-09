package model

import java.sql.Date

case class Patient(patient_id: Int, patient_name: String, date_of_birth: Date, contact_info: String)
