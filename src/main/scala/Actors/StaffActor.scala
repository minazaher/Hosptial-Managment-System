package Actors

import DAO.StaffDAO
import Model.Staff
import Table.StaffTable
import akka.actor.Actor
import slick.lifted.TableQuery

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.util.{Failure, Success}

case class insertStaffMember(member: Staff)
case class staffLogin(email: String)

class StaffActor(staffDAO: StaffDAO) extends Actor {
  val staffTable = TableQuery[StaffTable]

  override def receive: Receive = {
    case insertStaffMember(member) =>
      staffDAO.isMemberExist(member).flatMap { exists =>
        if (!exists) {
          staffDAO.insertStaffMember(member).map { _ =>
            println("Account Added")
          }.recover {
            case ex => println(s"Query Failed Because of: $ex")
          }
        } else {
          Future.successful(println("Member already exists"))
        }
      }
    case staffLogin(email) =>
      val originalSender = sender()

      println(email)

      val loginResult = staffDAO.login(email)
      loginResult.onComplete {
        case Success(Some(userId)) =>
          originalSender ! LoginSuccess(userId)

        case Success(None) =>
          originalSender ! LoginFailure("Account does not exist.")

        case Failure(ex) =>
          originalSender ! LoginFailure(s"$ex")
      }
  }
}

