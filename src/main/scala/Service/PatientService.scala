package Service

import DAO.PatientDAO
import Model.Doctor

import scala.concurrent.Future

class PatientService (patientDao:PatientDAO){

  def getPatientById(id: Int):  Future[Option[Doctor]]={
    patientDao.getPatientById(id)
  }
}
