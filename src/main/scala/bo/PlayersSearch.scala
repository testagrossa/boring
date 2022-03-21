package bo

object PlayersSearch {
  case class AccountStatusResponse(status: String, cause: Option[String])
  case class BrandResponse(code: String)
  case class PlayerDetailsResponse(uuid: String, brand: BrandResponse, accountStatus: AccountStatusResponse, countryCode: String)
  case class PlayerResponse(player: PlayerDetailsResponse)
  case class NodeResponse(node: PlayerResponse)
  case class PlayersSearchV2Response(edges: List[NodeResponse])
  case class DataViewerResponse(playersSearchV2: PlayersSearchV2Response)
  case class ViewerResponse(viewer: DataViewerResponse)

  def query(arg: String): String =
    s"""
       |query viewer {
       |  viewer{
       |    playersSearchV2($arg){
       |      edges{
       |        node{
       |          player{
       |            uuid
       |            countryCode
       |            brand {
       |              code
       |            }
       |            accountStatus {
       |              status
       |            }
       |          }
       |        }
       |      }
       |    }
       |  }
       |}
       |""".stripMargin
}
