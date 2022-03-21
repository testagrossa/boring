import bo.PlayersSearch
import io.circe.generic.auto._
import monix.eval.Task

import scala.util.Try

object Runner extends App {
  // file entry
  case class DinoPlayer(
    playerId: String,
    playerBrand: String,
    playerCountryCode: String,
    email: String,
    playerArg: String,
    fullLine: String
  )

  // task
  val boUri = "https://bo.infiniza.io/bo-graphql"
  val bearer = "eyJraWQiOiJfT1NjMFE2QWZBWXBhbHFseXJNR0dWNjhKaTBtMTYyTDVqaXlZa3gtdF84IiwiYWxnIjoiUlMyNTYifQ.eyJ2ZXIiOjEsImp0aSI6IkFULmJZQUpkTzRQcTRWMTZzMmczTWFXVXlRb3BsdUUzUU9PMS1rMEdiM2wtVHciLCJpc3MiOiJodHRwczovL2Rpbm90ZWNoLm9rdGEtZW1lYS5jb20vb2F1dGgyL2RlZmF1bHQiLCJhdWQiOiJhcGk6Ly9kZWZhdWx0IiwiaWF0IjoxNjM0MzAyNzExLCJleHAiOjE2MzQzMDYzMTEsImNpZCI6IjBvYTJvYmx5MzZHQ0lnRUNVMGk3IiwidWlkIjoiMDB1NGQ2eWI1NGtQSzFjSkEwaTciLCJzY3AiOlsib3BlbmlkIiwiZ3JvdXBzIiwiZW1haWwiXSwic3ViIjoiRnJhbmNvLlRlc3RhZ3Jvc3NhQGRpbm90ZWNoLmNvbSIsImdyb3VwcyI6WyJkYXRhLWRldmVsb3BlcnMiLCJhd3Mjb2t0YS1kYXRhZGV2IzEzNzEzMjI0NjU2MCIsIkV2ZXJ5b25lIiwiYXdzI2RhdGEtZGV2ZWxvcGVycyMxMzcxMzIyNDY1NjAiLCJhd3Mjb2t0YS1yb2FkbWluIzEzNzEzMjI0NjU2MCIsIkFjY2Vzc1JhcHRvciIsImJlLWRldmVsb3BlcnMiLCJEZXZlbG9wZXJzIiwiQWNjZXNzTW91bnRHb2xkIiwiQWNjZXNzUmVmdWVsQ2FzaW5vIiwiQ3VzdG9tZXJTdXBwb3J0IiwiQWNjZXNzQW1vayIsImF3c3ZwbiJdfQ.XPLs-zdPTFxXdvOAEfH8kW9eCt6ktMzWeoNxjXfNzPmA8BhXP-sTqP_il8OOF5UdvjIVTTljNfKbVWIrgYJBels-8nuqaAt5weoaArfD3Xrvog-eeq8tllh00AQL-edebnQtz2IWmV2thgp36oG078FnZ352-6r9T-AfGWHxrpDA5tqwQAtO4LDacnLe2B2ClIRc5_C9S1EfOxVaAZKHQiyWyDnsTAXsUB020HKRaqYXAfimFcY7ZkGCRd4LP-XLDpcwe6ZrwMHMrQwD9e-CjzjgCQTfdaIVFJdkJUe0USTvZtrmSuRwXKBcXvw6KAcAmZVhuEw8AN6108z-J15JRA"
  val fileName = "./data/amok/in/FI.csv"
  val writer = new FileWriter("./data/amok/out/FI_out_2.csv")

  import monix.execution.Scheduler.Implicits.global
  FileReader.readFile(fileName)(parseLine).foreach {
    case dinoPlayer@DinoPlayer(_, _, "FI", arg, _, _) =>
      HttpClient.postTask[PlayersSearch.ViewerResponse](boUri, bearer, PlayersSearch.query(s"ssn: \"$arg\", countryCode: \"FI\""))(task(dinoPlayer)).runSyncUnsafe()
    case dinoPlayer@DinoPlayer(_, _, countryCode, _, arg, _) if Set("SE", "CA", "NO").contains(countryCode) =>
      HttpClient.postTask[PlayersSearch.ViewerResponse](boUri, bearer, PlayersSearch.query(s"phoneNumber: \"$arg\""))(task(dinoPlayer)).runSyncUnsafe()
    case dinoPlayer =>
      println(s"Unable to process task for player: $dinoPlayer")
  }
  println(s"FINISHED")
  writer.close()

  // helpers
  private def parseLine(line: String, lineNumber: Int): Option[DinoPlayer] = {
    val playerDetails = line.split(',')
    val maybeFin = for {
      playerBrand <- Try(playerDetails(0))
      playerCountryCode <- Try(playerDetails(1))
      playerId <- Try(playerDetails(2))
      playerArg <- Try(playerDetails(3))
      email <- Try(playerDetails(4))
    } yield DinoPlayer(playerId, playerBrand, playerCountryCode, email, playerArg, line)
    if(maybeFin.isFailure) println(s"Failed to parse line ${lineNumber + 1}: $line")
    maybeFin.toOption
  }

  private def task(f: DinoPlayer)(r: PlayersSearch.ViewerResponse): Task[Unit] = {
    def checkPlayerAccountStatus(status: PlayersSearch.AccountStatusResponse): Boolean = {
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

    val linkedPlayers: Seq[PlayersSearch.PlayerDetailsResponse] = r.viewer.playersSearchV2.edges.filter { node =>
      val linkedPlayer = node.node.player
      linkedPlayer.brand.code != f.playerBrand
    }.map(_.node.player)

    val closedLinkedPlayers: Seq[PlayersSearch.PlayerDetailsResponse] = linkedPlayers.filter { player =>
      (checkPlayerAccountStatus(player.accountStatus) && player.brand.code == "AMOK") || player.brand.code == "RFLCN"
    }

    println(s"""DinoPlayer ${f.playerId} Got closed ${closedLinkedPlayers.size} => ${linkedPlayers.toList}""")
    if(closedLinkedPlayers.isEmpty) Task(writer.write(f.fullLine))
    else Task.unit
  }
}