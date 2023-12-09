package DAO
import Table._
import Model._
import slick.jdbc.MySQLProfile.api._

import scala.concurrent.Future

class DoctorDAO(val db: Database) {
  val doctors = TableQuery[DoctorTable]

  def getDoctorById(id: Int): Future[Option[Doctor]] = {
    db.run(doctors.filter(_.doctor_id === id).result.headOption)
  }

  def addDoctor(doctor: Doctor): Future[Int] = {
    db.run(doctors += doctor)
  }

}