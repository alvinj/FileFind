package filefind

import StringUtils._
import scala.io.Source
import scala.collection.mutable.ArrayBuffer

object FileUtils {

    /**
     * Only call this method when you know the `filename` contains matches.
     */
    def printMatchingLineNumbers(
        filename: String, 
        matchingLineNumbers: Seq[Int],
        theSearchPattern: String
    ): Unit = {
        // TODO handle Before and After
        val underline = makeUnderline(filename)
        println(s"\n${filename}\n${underline}")
        var lineNum = 0
        val bufferedSource = Source.fromFile(filename)
        for (line <- bufferedSource.getLines) {
            lineNum += 1
            if (matchingLineNumbers.contains(lineNum)) {
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