package uk.gov.hmrc.ngrnotify.model.bridge

import play.api.libs.json.{Format, Json}

// #/$defs/COMMON/POINTER
// #/$defs/ENTITIES/RELATIONSHIPS/ASSOCIATES
case class Pointer(
    transportation: Transportation,
    persistence: Persistence
)
object Pointer:
  given Format[Pointer] = Json.format



// ------------------------------
case class Transportation(
    path: Option[String]
)
object Transportation:
    given Format[Transportation] = Json.format



// ------------------------------
case class Persistence(
  // TODO place: Taxonomy_CAT-LTX-DOM
   place: String,
   identifier: Int
)
object Persistence:
  given Format[Persistence] = Json.format