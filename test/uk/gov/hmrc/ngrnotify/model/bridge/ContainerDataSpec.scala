/*
 * Copyright 2025 HM Revenue & Customs
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package uk.gov.hmrc.ngrnotify.model.bridge

import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.must.Matchers.mustBe
import play.api.libs.json.Json

class ContainerDataSpec extends AnyFreeSpec {
  "serialization and deserialization of ContainerData" in {
    val json = Json.parse("""
                            | {
                            |"foreign_ids": [
                            |  {
                            |    "system": "HMRC-VOA_CDB",
                            |    "location": "hmrc/voa/cdb/hereditament_vals",
                            |    "value": "24104677000"
                            |  }
                            |],
                            |"foreign_names": [],
                            |"foreign_labels": [],
                            |"uses": [],
                            |"constructions": [],
                            |"facilities": [],
                            |"artifacts": [
                            |  {
                            |    "activity": {
                            |      "source": null,
                            |      "value": null
                            |    },
                            |    "code": {
                            |      "source": null,
                            |      "value": null
                            |    },
                            |    "description": {
                            |      "source": "tom:sql/cdb:ndr/plant_machinery/os_refno:24104677000/description",
                            |      "value": "Plant and Machinery (Goods Lift)"
                            |    },
                            |    "quantity": {
                            |      "source": "tom:sql/cdb:ndr/plant_machinery/os_refno:24104677000/value",
                            |      "value": 2550
                            |    },
                            |    "units": {
                            |      "source": null,
                            |      "value": "GDP"
                            |    }
                            |  }
                            |],
                            |"uninheritances": [],
                            |"attributions": []
                            |}
                            |""".stripMargin)

    val model      = json.as[ContainerData]
    val serialized = Json.toJson(model)
    serialized mustBe json

  }
}
