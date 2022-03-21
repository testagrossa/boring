package bo

object RGMatching {
  case class AccountStatusResponse(status: String, cause: Option[String])
  case class BrandResponse(code: String)
  case class PlayerDetailsResponse(id: String, uuid: String, brand: BrandResponse, accountStatus: AccountStatusResponse, countryCode: String)
  case class PlayerResponse(player: PlayerDetailsResponse)
  case class NodeResponse(node: PlayerResponse)
  case class SearchPlayerMatchingResponsibleGamingResponse(edges: List[NodeResponse])
  case class DataViewerResponse(searchPlayerMatchingResponsibleGaming: SearchPlayerMatchingResponsibleGamingResponse)
  case class ViewerResponse(viewer: DataViewerResponse)

  def query(arg: String): String =
    s"""
       |query viewer {
       |  viewer{
       |    searchPlayerMatchingResponsibleGaming($arg){
       |      edges{
       |        node{
       |          player{
       |            id
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
