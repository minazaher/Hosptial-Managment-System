package Service

import DAO._
import Model._
import slick.jdbc.MySQLProfile.api._

import scala.concurrent.Future

class DoctorService(doctorDAO: DoctorDAO) {
  def getDoctorById(id: Int): Future[Option[Doctor]] = {
    doctorDAO.getDoctorById(id)
  }

}
