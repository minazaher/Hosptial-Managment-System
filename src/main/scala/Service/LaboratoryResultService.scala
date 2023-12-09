package Service


import DAO.LaboratoryResultDAO
import Model.LaboratoryResult
import slick.jdbc.MySQLProfile.api._

import scala.concurrent.Future

class LaboratoryResultService(laboratoryResultDAO: LaboratoryResultDAO) {
  def getLaboratoryResultById(id: Int): Future[Option[LaboratoryResult]] = {
    laboratoryResultDAO.getLaboratoryResultById(id)
  }

  def addLaboratoryResult(laboratoryResult: LaboratoryResult): Future[Int] = {
    laboratoryResultDAO.addLaboratoryResult(laboratoryResult)
  }

  def getLaboratoryResultsForPatient(patientId: Int): Future[Seq[LaboratoryResult]] = {
    laboratoryResultDAO.getLaboratoryResultsForPatient(patientId)
  }

  // Implement other business logic related to LaboratoryResult
}