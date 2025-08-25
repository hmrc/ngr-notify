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

package uk.gov.hmrc.ngrnotify.infrastructure

import com.google.inject.ImplementedBy
import com.typesafe.config.Config
import org.apache.pekko.actor.ActorSystem
import play.api.Configuration
import play.api.http.ContentTypes
import play.api.libs.ws.{BodyWritable, InMemoryBody, WSClient, WSProxyServer}
import play.api.mvc.Codec
import uk.gov.hmrc.http.hooks.HttpHook
import uk.gov.hmrc.http.{HttpPost, HttpResponse}
import uk.gov.hmrc.play.http.ws.*

import java.net.URLEncoder
import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

// TODO Use HttpClientV2 instead of HttpPost, WSPost and WSProxy
trait HodHttpClient extends HttpPost with WSPost

@Singleton
class HodHttpClientImpl @Inject() (config: Configuration, client: WSClient, ac: ActorSystem)
    extends HodHttpClient
    with WSProxy {

  override def wsClient: WSClient = client

  override protected def actorSystem: ActorSystem = ac

  override protected def configuration: Config = config.underlying

  override val hooks: Seq[HttpHook] = Seq.empty

  override def wsProxyServer: Option[WSProxyServer] = WSProxyConfiguration.buildWsProxyServer(config)

  override def doFormPost(
    url: String,
    body: Map[String, Seq[String]],
    headers: Seq[(String, String)]
  )(implicit ec: ExecutionContext): Future[HttpResponse] = {

    val voaForCodec = Codec.javaSupported("x-voa-for")

    buildRequest(url, headers)
      .post(body)(using newUrlEncode(voaForCodec))
      .map(r =>
        HttpResponse(
          r.status,
          r.body,
          r.headers.map { case (name, values) =>
            name -> values.toSeq
          }
        )
      )
  }

  private def newUrlEncode(codec: Codec): BodyWritable[Map[String, Seq[String]]] =
    BodyWritable(
      formData =>
        InMemoryBody(
          codec.encode(
            formData.flatMap(item => item._2.map(c => item._1 + "=" + URLEncoder.encode(c, "x-voa-for"))).mkString("&")
          )
        ),
      ContentTypes.FORM
    )

}

@ImplementedBy(classOf[MdtpHttpClientImpl])
trait MdtpHttpClient extends HttpPost with WSPost {}

@Singleton
class MdtpHttpClientImpl @Inject() (val actorSystem: ActorSystem, conf: Configuration, client: WSClient)
    extends MdtpHttpClient {

  override protected def configuration: Config = conf.underlying

  override val hooks: Seq[HttpHook] = Seq.empty

  override def wsClient: WSClient = client
}
