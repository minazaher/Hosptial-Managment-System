import DAO.DoctorDAO
import Service.DoctorService
import config.Connection

class Main extends App {

  val db = Connection.db;

  val doctorDAO = new DoctorDAO(db)
  val doctorService = new DoctorService(doctorDAO)

}
