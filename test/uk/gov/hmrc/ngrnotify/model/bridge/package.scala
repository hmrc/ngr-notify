package uk.gov.hmrc.ngrnotify.model

package object bridge {

  def testResourceContent(resource: String): String =
    scala.io.Source.fromResource(resource).getLines().mkString("\n")
}
