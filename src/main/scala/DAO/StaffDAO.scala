package DAO

import Model.{Appointment, Doctor, Staff}
import slick.jdbc.MySQLProfile.api._
import _root_.Table.StaffTable
import slick.lifted.TableQuery

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class StaffDAO(val db: Database) {

  val staffMembers = TableQuery[StaffTable]

  def insertStaffMember(staffMember: Staff):Future[Int] = {
    db.run(staffMembers += staffMember)
  }
  def login(email: String): Future[Option[Int]] = {
    val query = staffMembers.filter(_.contact_info === email).map(_.staff_id).result.headOption
    db.run(query)
  }

  def isMemberExist(member: Staff): Future[Boolean] = {
    db.run(staffMembers.filter(_.contact_info === member.contact_info).result.headOption).map(_.isDefined)
  }

}
