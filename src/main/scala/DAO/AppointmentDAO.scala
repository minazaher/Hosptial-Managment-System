package DAO


import Model.Appointment

import scala.concurrent.Future
import slick.jdbc.MySQLProfile.api._
import _root_.Table.AppointmentTable


class AppointmentDAO(val db: Database) {
  val appointments = TableQuery[AppointmentTable]

  def getAppointmentById(id: Int): Future[Option[Appointment]] = {
    db.run(appointments.filter(_.appointment_id === id).result.headOption)
  }

  def addAppointment(appointment: Appointment): Future[Int] = {
    db.run(appointments += appointment)
  }
  def getAppointmentsForDoctor(doctorId: Int): Future[Seq[Appointment]] = {
    db.run(appointments.filter(_.doctor_id === doctorId).result)
  }

}
