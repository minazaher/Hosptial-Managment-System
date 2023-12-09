package model

case class MedicalRecord(record_id: Int, patient_id: Int, vital_signs: String, medical_history: String, medication_details: String)
