package Service


import DAO.BillingInformationDAO
import Model.BillingInformation
import slick.jdbc.MySQLProfile.api._

import scala.concurrent.Future

class BillingInformationService(billingInformationDAO: BillingInformationDAO) {
  def getBillingInformationById(id: Int): Future[Option[BillingInformation]] = {
    billingInformationDAO.getBillingInformationById(id)
  }

  def addBillingInformation(billingInformation: BillingInformation): Future[Int] = {
    billingInformationDAO.addBillingInformation(billingInformation)
  }

}
