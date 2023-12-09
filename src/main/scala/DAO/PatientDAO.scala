package DAO

import Model.Patient
import Table.PatientTable
import slick.jdbc.MySQLProfile.api._
import slick.lifted.TableQuery

import scala.concurrent.Future

class PatientDAO(val db: Database) {

  val patients = TableQuery[PatientTable]

  def getPatientById(id: Int): Future[Option[Patient]] = {
    db.run(patients.filter(_.patient_id === id).result.headOption)
  }
  def insertPatient(patient: Patient): Future[Int] = {
    db.run(patients += patient)
  }

}