package Service

import DAO.PatientDAO
import Model.Patient

import scala.concurrent.Future

class PatientService (patientDao:PatientDAO){

  def getPatientById(id: Int):  Future[Option[Patient]]={
    patientDao.getPatientById(id)
  }

  def insertPatient(patient: Patient): Future[Int] = {
    patientDao.insertPatient(patient);
  }
}
