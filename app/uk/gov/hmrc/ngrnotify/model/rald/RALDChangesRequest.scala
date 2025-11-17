package uk.gov.hmrc.ngrnotify.model.rald

import play.api.libs.json.{Json, OFormat}

//TODO ADD ACTUAL MODEL WHEN FRONTEND IS COMPLETE
case class RALDChangesRequest(todo: Option[String] = None)

object RALDChangesRequest:
  implicit val format: OFormat[RALDChangesRequest] = Json.format
