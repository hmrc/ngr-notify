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

package uk.gov.hmrc.ngrnotify.metrics

import com.codahale.metrics.{Meter, MetricRegistry}
import org.mockito.Mockito.when
import org.scalatest.freespec.AnyFreeSpec
import org.scalatest.matchers.must.Matchers
import org.scalatestplus.mockito.MockitoSugar.mock

class MetricsHandlerSpec extends AnyFreeSpec with Matchers{
  "MetricsHandler" - {
    "must initialize all meters with correct names" in {
      val mockRegistry = mock[MetricRegistry]
      val failedMeter = mock[Meter]
      val okMeter = mock[Meter]
      val refNumMeter = mock[Meter]
      val importedMeter = mock[Meter]

      when(mockRegistry.meter("failedforsubmissions")).thenReturn(failedMeter)
      when(mockRegistry.meter("okforsubmissions")).thenReturn(okMeter)
      when(mockRegistry.meter("requestRefNumSubmissions")).thenReturn(refNumMeter)
      when(mockRegistry.meter("importedcredentials")).thenReturn(importedMeter)

      val handler = new MetricsHandler(mockRegistry)

      handler.failedSubmissions mustBe failedMeter
      handler.okSubmissions mustBe okMeter
      handler.requestRefNumSubmissions mustBe refNumMeter
      handler.importedCredentials mustBe importedMeter
    }
  }
}
