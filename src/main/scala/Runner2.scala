import bo.{RGMatching, SelfExclusionDetails}
import io.circe.generic.auto._
import monix.eval.Task

import scala.util.Try

object Runner2 extends App {
  val boUri = "https://bo.infiniza.io/bo-graphql"
  val bearer = "eyJraWQiOiJfT1NjMFE2QWZBWXBhbHFseXJNR0dWNjhKaTBtMTYyTDVqaXlZa3gtdF84IiwiYWxnIjoiUlMyNTYifQ.eyJ2ZXIiOjEsImp0aSI6IkFULjRLcE1pX2dVUUcydXZ0Q0pRUFFKaUR4OV9ia2d1a2RteEVzVHd0SmR1dTQiLCJpc3MiOiJodHRwczovL2Rpbm90ZWNoLm9rdGEtZW1lYS5jb20vb2F1dGgyL2RlZmF1bHQiLCJhdWQiOiJhcGk6Ly9kZWZhdWx0IiwiaWF0IjoxNjM0MzIwODM3LCJleHAiOjE2MzQzMjQ0MzcsImNpZCI6IjBvYTJvYmx5MzZHQ0lnRUNVMGk3IiwidWlkIjoiMDB1NGQ2eWI1NGtQSzFjSkEwaTciLCJzY3AiOlsib3BlbmlkIiwiZ3JvdXBzIiwiZW1haWwiXSwic3ViIjoiRnJhbmNvLlRlc3RhZ3Jvc3NhQGRpbm90ZWNoLmNvbSIsImdyb3VwcyI6WyJkYXRhLWRldmVsb3BlcnMiLCJhd3Mjb2t0YS1kYXRhZGV2IzEzNzEzMjI0NjU2MCIsIkV2ZXJ5b25lIiwiYXdzI2RhdGEtZGV2ZWxvcGVycyMxMzcxMzIyNDY1NjAiLCJhd3Mjb2t0YS1yb2FkbWluIzEzNzEzMjI0NjU2MCIsIkFjY2Vzc1JhcHRvciIsIkNNUyIsIkFkanVzdG1lbnRzIiwiYmUtZGV2ZWxvcGVycyIsIkRldmVsb3BlcnMiLCJBY2Nlc3NNb3VudEdvbGQiLCJBY2Nlc3NSZWZ1ZWxDYXNpbm8iLCJDdXN0b21lclN1cHBvcnQiLCJBY2Nlc3NBbW9rIiwiYXdzdnBuIl19.S9ddbinQkys6eGpU6-2yYeVqjRhXT7xGhDhVehP0t3mvW4jwpmmAk-T3XmDWUzRNyMc5z86MEW3KaqVmzGZ7Up8wPJXdz2jHcz0uBzhCUoo65LJy-fL1tE-4xffQEk9kywAVlB2cWJkMmiIr7GGCDndAtXJCMDimzRSlZz9S_Qq8XIptIVbgHaY-pBnbfEpUl1yJ1Y0QypTeL2Ib7x-9H5EOlmPQzKHFgEpn5Yrk-hj1sa0eQ9DdELrLz_lb8MVWa72qORfk8AsFI5N55gO-W4okg9Z7MTpJc171dqMa_e6m5AXgcvVxSRlPE-T3R5qzsAW7qapTk_wJmfiWQEZRUw"
  val inputFileName = "./data/self/in/CanadianRefuelFails.csv"
  val outputFileName = "./data/self/out/CanadianRefuelFails_out.csv"

  run()

  // file entry
  case class DinoPlayer(
    brand: String,
    countryCode: String,
    id: String,
    firstName: String,
    lastName: String,
    dateOfBirth: String,
    phoneNumber: String,
    email: String,
    fullLine: String
  )

  def run(): Unit = {
    import monix.execution.Scheduler.Implicits.global
    val writer = new FileWriter(outputFileName)
    FileReader.readFile(inputFileName)(parseLine).foreach { dinoPlayer =>
      val arg =
        s"""
           |firstName: "${dinoPlayer.firstName}",
           |lastName: "${dinoPlayer.lastName}",
           |birthDate: "${dinoPlayer.dateOfBirth}",
           |email: "${dinoPlayer.email}",
           |phoneNumber: "${dinoPlayer.phoneNumber}"
           |""".stripMargin
      val query = RGMatching.query(arg)

      HttpClient.postResponse[RGMatching.ViewerResponse](boUri, bearer, query)
        .map(toLinkedPlayer(dinoPlayer))
        .map { linkedPlayers =>
          val closedLinkedPlayers = linkedPlayers.filter(isPlayerClosed)
          println(s"""DinoPlayer ${dinoPlayer.id} Got closed ${closedLinkedPlayers.size} [${closedLinkedPlayers.map(_.id)}] => $linkedPlayers""")
          if(closedLinkedPlayers.isEmpty) Task(writer.write(dinoPlayer.fullLine))
          else Task.unit
        }.runSyncUnsafe()
    }
    println(s"FINISHED")
    writer.close()
  }

  // helpers
  private def parseLine(line: String, lineNumber: Int): Option[DinoPlayer] = {
    val playerDetails = line.split(',')
    val maybeFin = for {
      brand <- Try(playerDetails(0))
      countryCode <- Try(playerDetails(1))
      id <- Try(playerDetails(2))
      firstName <- Try(playerDetails(3))
      lastName <- Try(playerDetails(4))
      phoneNumber <- Try(playerDetails(5))
      email <- Try(playerDetails(6))
      dateOfBirth <- Try(playerDetails(7)) // Try(playerDetails(13))
      birthDate = {
        val tokens = dateOfBirth.split('/')
        s"${tokens(2)}-${tokens(1)}-${tokens(0)}"
      }
    } yield DinoPlayer(brand, countryCode, id, firstName, lastName, birthDate, phoneNumber, email, line)
    if(maybeFin.isFailure) println(s"Failed to parse line ${lineNumber + 1}: $line")
    maybeFin.toOption
  }

  private def toLinkedPlayer(f: DinoPlayer)(r: RGMatching.ViewerResponse): List[RGMatching.PlayerDetailsResponse] = {
    val linkedPlayers = r.viewer.searchPlayerMatchingResponsibleGaming.edges.filter { node =>
      val linkedPlayer = node.node.player
      linkedPlayer.brand.code != f.brand
    }.map(_.node.player)
    linkedPlayers
  }

  private def isPlayerClosed(player: RGMatching.PlayerDetailsResponse): Boolean = {
    def checkPlayerAccountStatus(status: RGMatching.AccountStatusResponse): Boolean = {
      val forbidenCloseCauses = Set(
        "AbusiveBehaviour",
        "ConfirmedFraud",
        "ConfirmedPromotionAbuse",
        "MultipleAccounts",
        "Other",
        "PEPSanctionsListMatch",
        "ResponsibleGaming",
        "SanctionsListMatch",
        "TemporaryCustomerRequest",
        "PermanentCustomerRequest",
      )
      status.cause.exists(forbidenCloseCauses.contains)
    }
    checkPlayerAccountStatus(player.accountStatus)
  }
}