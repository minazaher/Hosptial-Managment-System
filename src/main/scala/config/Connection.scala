package config
import slick.jdbc.MySQLProfile.api._

object Connection {
  val db = Database.forConfig("hospital")

}
