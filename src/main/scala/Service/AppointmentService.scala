package Service

import DAO.AppointmentDAO
import Model.Appointment
import slick.jdbc.MySQLProfile.api._

import scala.concurrent.Future

class AppointmentService(appointmentDAO: AppointmentDAO) {
  def getAppointmentById(id: Int): Future[Option[Appointment]] = {
    appointmentDAO.getAppointmentById(id)
  }

  def addAppointment(appointment: Appointment): Future[Int] = {
    appointmentDAO.addAppointment(appointment)
  }

  def getAppointmentsForDoctor(doctorId: Int): Future[Seq[Appointment]] = {
    appointmentDAO.getAppointmentsForDoctor(doctorId)
  }

  // Implement other business logic related to Appointment
}
