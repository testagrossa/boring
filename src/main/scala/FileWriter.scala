import java.io._

class FileWriter(fileName: String) {
  private val pw = new PrintWriter(new File(fileName ))

  def close(): Unit = pw.close()
  def write(s: String): Unit = pw.write(s + "\n")
}
