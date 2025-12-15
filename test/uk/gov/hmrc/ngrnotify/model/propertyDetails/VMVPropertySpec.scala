package uk.gov.hmrc.ngrnotify.model.propertyDetails

import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.must.Matchers.mustBe

class VMVPropertySpec extends AnyFreeSpec with JobMessageTestData {
  "VMVPropertySpec" - {
    "VMVProperty toString should redact sensitive information" in {
      val toStringOutput = vmvProperty.toString
      toStringOutput mustBe "uarn: 100, addressFull: property-id, localAuthorityCode: address, localAuthorityReference: LA123, valuations: [assessmentRef: 12345, assessmentStatus: CURRENT, rateableValue: None, scatCode: Some(Details about valuation), descriptionText: description, effectiveDate: 2025-12-15, currentFromDate: 2025-12-15, listYear: 2023, primaryDescription: primary, allowedActions: [], listType: type, propertyLinkEarliestStartDate: None]"
    }
  }

}
