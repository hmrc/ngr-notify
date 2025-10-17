package uk.gov.hmrc.ngrnotify.model.bridge

import play.api.libs.json.Json
import uk.gov.hmrc.ngrnotify.backend.base.AnyWordAppSpec

class PostRatepayerBodySpec extends AnyWordAppSpec {
  "Model PostRatepayerBodySpec" should {
    "be serialised from JSON" in {
      val postRatepayerJson = Json.parse(testResourceContent("post-ratepayer-example.json"))
      val postRatepayer = postRatepayerJson.as[PostRatepayer]

      Json.toJson(postRatepayer).as[PostRatepayer] shouldBe postRatepayer
    }
  }
}
