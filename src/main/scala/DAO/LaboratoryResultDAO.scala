package DAO
import Model.LaboratoryResult
import Table.LaboratoryResultTable
import slick.jdbc.MySQLProfile.api._

import scala.concurrent.Future

class LaboratoryResultDAO(val db: Database) {
  val laboratoryResults = TableQuery[LaboratoryResultTable]

  def getLaboratoryResultById(id: Int): Future[Option[LaboratoryResult]] = {
    db.run(laboratoryResults.filter(_.result_id === id).result.headOption)
  }

  def addLaboratoryResult(laboratoryResult: LaboratoryResult): Future[Int] = {
    db.run(laboratoryResults += laboratoryResult)
  }

  def getLaboratoryResultsForPatient(patientId: Int): Future[Seq[LaboratoryResult]] = {
    db.run(laboratoryResults.filter(_.patient_id === patientId).result)
  }
}