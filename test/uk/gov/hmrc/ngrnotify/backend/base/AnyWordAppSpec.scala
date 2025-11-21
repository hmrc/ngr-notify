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

package uk.gov.hmrc.ngrnotify.backend.base

import org.mockito.ArgumentMatchers
import org.scalatest.wordspec.AnyWordSpec
import org.scalatestplus.play.guice.GuiceOneAppPerSuite

import java.net.URL
import scala.io.Source

/**
  * @author Yuriy Tumakha
  */
class AnyWordAppSpec extends AnyWordSpec with GuiceOneAppPerSuite with AppSuiteBase:

  def testResourceContent(resource: String): String =
    Source.fromResource(resource).mkString


  def endingWith(s: String): URL = new URL(s"http://whatever:9999$s")

  def url[T](value: T): T = ArgumentMatchers.refEq(value,
    "protocol",
    "host",
    "port",
    "file",
    "query",
    "authority",
    // "path",
    "userInfo",
    "ref",
    "hostAddress",
    "handler",
    "hashCode",
    "tempState"
  )
