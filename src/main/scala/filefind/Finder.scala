package filefind

import java.io._
import java.nio.file._
import java.nio.file.attribute._
//import java.util.
import java.nio.file.FileVisitResult._  //static
import java.nio.file.FileVisitOption._  //static
import scala.collection.mutable.ArrayBuffer
import scala.io.Source

class Finder (filePattern: String, searchPattern: String)
extends SimpleFileVisitor[Path] {

    var matcher: PathMatcher = null
    var numMatches = 0
    //private final PathMatcher matcher;
    //private int numMatches = 0;

    // "glob:" is part of the syntax
    // https://docs.oracle.com/javase/8/docs/api/java/nio/file/FileSystem.html#getPathMatcher-java.lang.String-
    matcher = FileSystems.getDefault()
                .getPathMatcher("glob:" + filePattern);

    // compares the glob filePattern against the file or directory name
    def find(file: Path): Unit = {
        val name: Path = file.getFileName()

        if (name != null && matcher.matches(name)) {
            numMatches += 1
            val canonFilename = file.toAbsolutePath.toString
            val matchingLineNumbers = findMatchingLineNumbers(canonFilename, searchPattern)
            if (findMatchingLineNumbers(canonFilename, searchPattern).size > 0) {
                printMatchingLineNumbers(canonFilename, matchingLineNumbers, searchPattern)
            }
        }

        /**
         * What I Really Want:
         *     - if the file matches the pattern
         *     - find all lines in the file that match (matchingLineNumbers:List[Int])
         *     - go back through the file and get the before and after lines
         *       as well as the desired line
         *     - print the output as desired
         */
    }

    /**
     * Create an “underline” string that’s the same length as the input string.
     * {{{
     *     val s = "foobar"
     *     val u = makeUnderline(s)
     *     println(u)  // "------"
     * }}}
     */
    private def makeUnderline(s: String) = List.fill(s.length)('-').mkString

    /**
     * Only call this method when you know the `filename` contains matches.
     */
    private def printMatchingLineNumbers(
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

    /**
     * Highlights the search pattern within the string `line`.
     * It currently makes the search pattern bold and underlined.
     * The output string is intended to be shown in an ANSI terminal,
     * and I can confirm that it works with the MacOS Terminal.
     * 
     * More info on escape sequences: 
     * stackoverflow.com/questions/4842424/list-of-ansi-color-escape-sequences
     */
    private def highlightSearchPatternForAnsiTerminals(line: String, theSearchPattern: String): String =
        line.replaceAll(
            theSearchPattern,
            s"\033[1;4m${theSearchPattern}\033[0m"
        )

    private def findMatchingLineNumbers(filename: String, pattern: String): Seq[Int] = {
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

    // prints the total number of matches to standard out
    def done() = {
        println("Matched: " + numMatches)
    }
    
    // invoke the filePattern matching method on each file
    override def visitFile(file: Path, attrs: BasicFileAttributes): FileVisitResult = {
        find(file)
        return CONTINUE
    }

    // invoke the filePattern matching method on each directory
    override def preVisitDirectory(dir: Path, attrs: BasicFileAttributes): FileVisitResult = {
        find(dir)
        return CONTINUE
    }

    override def visitFileFailed(file: Path, ioe: IOException): FileVisitResult = {
        System.err.println(ioe)
        return CONTINUE
    }
    

}
