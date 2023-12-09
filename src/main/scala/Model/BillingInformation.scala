package Model

import java.sql.Date

case class BillingInformation(bill_id: Int, patient_id: Int, billing_date: Date, total_amount: BigDecimal, payment_status: String)
