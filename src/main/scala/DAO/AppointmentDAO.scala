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
  def getAppointmentsByStatus(status: String): Future[Seq[Appointment]] = {
    val query = appointments.filter(_.status === status).result
    db.run(query)
  }

  def approveAppointment(appointmentId: Int, staffId: Int) :Future[Int] ={
    val query = appointments
      .filter(_.appointment_id === appointmentId)
      .map(app => (app.status, app.approved_by))
      .update(("Approved", staffId))
    db.run(query)
  }
}
