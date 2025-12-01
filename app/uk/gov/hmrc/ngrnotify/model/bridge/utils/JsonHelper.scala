package uk.gov.hmrc.ngrnotify.model.bridge.utils

object JsonHelper {

  object bridge {
    case class NullableValue[T](value: Option[T])

    import play.api.libs.json._

    object NullableValue {
      implicit def format[T: Format]: Format[NullableValue[T]] = Format(
        {
          case JsNull => JsSuccess(NullableValue(None))
          case json => implicitly[Format[T]].reads(json).map(v => NullableValue(Some(v)))
        },
        nv => nv.value.map(implicitly[Format[T]].writes).getOrElse(JsNull)
      )
    }
  }
}
