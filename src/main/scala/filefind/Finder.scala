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
                printMatchingLineNumbers(canonFilename, matchingLineNumbers)
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
     *    val s = "foobar"
     *    val u = makeUnderline(s)
     *    println(u) // "------"
     * }}}
     */
    private def makeUnderline(s: String) =  List.fill(s.length)('-').mkString

    /**
     * Only call this method when you know the `filename` contains matches.
     */
    private def printMatchingLineNumbers(filename: String, matchingLineNumbers: Seq[Int]): Unit = {
        val underline = makeUnderline(filename)
        println("")
        println(filename)
        println(underline)
        var lineNum = 0
        val bufferedSource = Source.fromFile(filename)
        for (line <- bufferedSource.getLines) {
            lineNum += 1
            if (matchingLineNumbers.contains(lineNum)) {
                //TODO only make the matching word/pattern bold
                println(s"\033[1m${line}\033[0m")
            }
        }
        bufferedSource.close
        println("")
    }


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
