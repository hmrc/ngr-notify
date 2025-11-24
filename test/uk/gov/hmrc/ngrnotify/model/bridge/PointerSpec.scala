package uk.gov.hmrc.ngrnotify.model.bridge

import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.must.Matchers.mustBe
import play.api.libs.json.Json

class PointerSpec extends AnyFreeSpec {
  "Pointer" - {
    "serialization" - {
      "must serialize to JSON string" in {
        val pointer    = Pointer(PointerTransportation(Some("/some/path")), persistence = PointerPersistence(place = "SomePlace", identifier = 12345))
        val jsonString = """{"transportation":{"path":"/some/path"},"persistence":{"place":"SomePlace","identifier":12345}}"""
        val json       = uk.gov.hmrc.ngrnotify.model.bridge.Pointer.pointerFormat.writes(pointer).toString()
        json mustBe jsonString
      }

      "must deserialize from JSON string" in {
        val jsonString = Json.parse("""{"transportation":{"path":"/some/path"},"persistence":{"place":"SomePlace","identifier":12345}}""".stripMargin)
        jsonString.as[Pointer] mustBe Pointer(
          PointerTransportation(Some("/some/path")),
          persistence = PointerPersistence(place = "SomePlace", identifier = 12345)
        )
      }
    }
  }
}
