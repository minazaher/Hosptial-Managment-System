package Model

case class MedicalRecord(record_id: Int, patient_id: Int, vital_signs: String, medical_history: String, medication_details: String){
  def formattedString: String = {
    s"""
       |Medical Record ID: $record_id
       |Patient ID: $patient_id
       |Vital Signs: $vital_signs
       |Medical History: $medical_history
       |Medication Details: $medication_details
       |""".stripMargin
  }
}
