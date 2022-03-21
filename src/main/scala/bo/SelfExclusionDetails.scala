package bo

object SelfExclusionDetails {
  case class PendingSelfExclusionResponse(
    exclusionEndsAt: Option[String],
    configuredAt: String,
    activeFrom: String
  )
  case class SelfExclusionDetailsResponse(
    configuredAt: Option[String],
    exclusionEndsAt: Option[String],
    willBeCancelledAt: Option[String],
    pending: Option[PendingSelfExclusionResponse]
  )
  case class DataViewerResponse(selfExclusionDetails: Option[SelfExclusionDetailsResponse])
  case class ViewerResponse(player: DataViewerResponse)

  def query(arg: String): String =
    s"""
       |query GetSelfExclusion {
       |  player($arg) {
       |		selfExclusionDetails {
       |      exclusionEndsAt
       |      willBeCancelledAt
       |      configuredAt
       |      pending {
       |        exclusionEndsAt
       |        configuredAt
       |        activeFrom
       |      }
       |    }
       |  }
       |}
       |""".stripMargin
}
