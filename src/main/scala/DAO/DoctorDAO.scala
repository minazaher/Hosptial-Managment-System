package DAO
import Table._
import Model._
import slick.jdbc.MySQLProfile.api._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class DoctorDAO(val db: Database) {
  val doctors = TableQuery[DoctorTable]

  def getDoctorById(id: Int): Future[Option[Doctor]] = {
    db.run(doctors.filter(_.doctor_id === id).result.headOption)
  }

  def addDoctor(doctor: Doctor): Future[Int] = {
    db.run(doctors += doctor)
  }

  def login(email: String): Future[Option[Int]] = {
    db.run(doctors.filter(_.contact_info === email).map(_.doctor_id).result.headOption)
  }

  def doesDoctorExist(doctor: Doctor): Future[Boolean] = {
    db.run(doctors.filter(_.doctor_name === doctor.doctor_name).result.headOption).map(_.isDefined)
  }
  def getAppointments(doctorId: Int): Future[Seq[Appointment]] = {
    val appointments = TableQuery[AppointmentTable]
    db.run(appointments.filter(_.doctor_id === doctorId).filter(_.status === "Approved").result)
  }

}