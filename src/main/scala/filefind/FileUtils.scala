package filefind

import StringUtils._
import scala.io.Source
import scala.collection.mutable.ArrayBuffer

case class LineRange(firstLine: Int, lastLine: Int)

object FileUtils {

    /**
     * Only call this method when you know the `filename` contains matches.
     */
    def printMatchingLineNumbers(
        filename: String, 
        lineNumsWherePatternFound: Seq[Int],
        theSearchPattern: String,
        linesBefore: Int,
        linesAfter: Int
    ): Unit = {

        val allLinesToPrint = ArrayBuffer[Int]()

        for (lineNum <- lineNumsWherePatternFound) {
            val firstLine = lineNum - linesBefore  //TODO could be < 0
            val lastLine = lineNum + linesAfter    //TODO could be > file_length
            if (firstLine == lastLine) {
                allLinesToPrint += firstLine
            } else {
                val newLines = Range(firstLine, lastLine).toList  //(10,14)
                allLinesToPrint ++= newLines
            }
        }

        val underline = makeUnderline(filename)
        println(s"\n${filename}\n${underline}")
        var lineNum = 0
        val bufferedSource = Source.fromFile(filename)
        for (line <- bufferedSource.getLines) {
            lineNum += 1
            if (allLinesToPrint.contains(lineNum)) {
                println(highlightSearchPatternForAnsiTerminals(line, theSearchPattern))
            }
        }
        bufferedSource.close
    }

    def findMatchingLineNumbers(filename: String, pattern: String): Seq[Int] = {
        val matchingLineNumbers = ArrayBuffer[Int]()
        var lineNum = 0
        val bufferedSource = Source.fromFile(filename)
        for (line <- bufferedSource.getLines) {
            lineNum += 1
            if (line.contains(pattern)) matchingLineNumbers += lineNum
        }
        bufferedSource.close
        matchingLineNumbers.toSeq
    }

}