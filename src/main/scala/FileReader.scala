object FileReader {
  def readFile[A](fileName: String)(parser: (String, Int) => Option[A]): List[A] = {
    val file = scala.io.Source.fromFile(fileName)
    file.getLines().toList.zipWithIndex.flatMap { case (l, i) => parser(l, i) }
  }
}
