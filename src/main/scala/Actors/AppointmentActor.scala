package Actors

import DAO.{AppointmentDAO, DoctorDAO}
import Model.Appointment
import akka.actor.{Actor, ActorLogging}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.{Failure, Success}

case class getAppointmentById(id: Int)
case class addAppointment(appointment: Appointment)
case class  getAppointmentsForDoctor(doctorId: Int)

class AppointmentActor(dao: AppointmentDAO) extends Actor{

  override def receive: Receive = {
    case getAppointmentById(id) =>
      dao.getAppointmentById(id).onComplete {
        case Success(doc) => println("Account Added")
        case Failure(ex) => println(s"Query Failed Because of : $ex")
      }
    case addAppointment(appointment)=>
      dao.addAppointment(appointment).onComplete{
        case Success(doc) => println("Appointment Added")
        case Failure(ex) => println(s"Query Failed Because of : $ex")
      }
    case getAppointmentsForDoctor(doctorId) =>
      dao.getAppointmentsForDoctor(doctorId).onComplete
      {
        case Success(appointments) =>
          appointments match {
            case Nil =>
              println("No appointments found for this doctor.")
            case _ =>
              println(s"Appointments for doctor with ID : $doctorId:")
              appointments.foreach(appointment => println(appointment.toString))
          }
        case Failure(ex) =>
          println(s"Query failed because of: $ex")
      }
  }
}