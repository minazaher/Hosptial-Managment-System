package Table

import Model.BillingInformation
import slick.jdbc.MySQLProfile.api._
import java.sql.Date

class BillingInformationTable(tag: Tag) extends Table[BillingInformation](tag, "BillingInformation") {
  def bill_id = column[Int]("bill_id", O.PrimaryKey)
  def patient_id = column[Int]("patient_id")
  def billing_date = column[Date]("billing_date")
  def total_amount = column[BigDecimal]("total_amount")
  def payment_status = column[String]("payment_status")

  override def * = (bill_id, patient_id, billing_date, total_amount, payment_status) <> (BillingInformation.tupled, BillingInformation.unapply)

  // Foreign key reference to Patients table
  def patient = foreignKey("patient_fk", patient_id, TableQuery[PatientTable])(_.patient_id)
}
