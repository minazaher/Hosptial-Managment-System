package DAO


import Model.BillingInformation
import Table.BillingInformationTable
import slick.jdbc.MySQLProfile.api._

import scala.concurrent.Future

class BillingInformationDAO(val db: Database) {
  val billingInformations = TableQuery[BillingInformationTable]

  def getBillingInformationById(id: Int): Future[Option[BillingInformation]] = {
    db.run(billingInformations.filter(_.bill_id === id).result.headOption)
  }

  def addBillingInformation(billingInformation: BillingInformation): Future[Int] = {
    db.run(billingInformations += billingInformation)
  }

}
