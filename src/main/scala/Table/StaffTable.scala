package Table

import Model.Staff

import slick.jdbc.MySQLProfile.api._

class StaffTable(tag: Tag) extends Table[Staff](tag, "staff"){
  def staff_id = column[Int]("staff_id", O.PrimaryKey, O.AutoInc)
  def staff_name = column[String]("staff_name")
  def role = column[String]("role")
  def contact_info = column[String]("contact_info")

  override def * = (staff_id, staff_name, role, contact_info) <> (Staff.tupled, Staff.unapply)
}
