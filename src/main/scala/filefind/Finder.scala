package filefind

import java.io._
import java.nio.file._
import java.nio.file.attribute._
import java.nio.file.FileVisitResult._
import java.nio.file.FileVisitOption._
import scala.io.Source
import StringUtils._
import FileUtils._

class Finder (filePattern: String, searchPattern: String, linesBefore: Int, linesAfter: Int)
extends SimpleFileVisitor[Path] {

    var pathMatcher: PathMatcher = null
    var numFilenameMatches = 0
    var numPatternMatches = 0

    // note: "glob:" is part of the syntax
    // https://docs.oracle.com/javase/8/docs/api/java/nio/file/FileSystem.html#getPathMatcher-java.lang.String-
    pathMatcher = FileSystems.getDefault()
        .getPathMatcher("glob:" + filePattern)

    // compares the glob filePattern against the file or directory name
    def find(file: Path): Unit = {
        val name: Path = file.getFileName()
        if (name != null && pathMatcher.matches(name)) {
            // TODO the file-reading code here can throw an exception, such as on
            // _main.dart binary files, so this is a temporary kludge that will
            // probably last a long time.
            numFilenameMatches += 1
            val canonFilename = file.toAbsolutePath.toString
            try {
                val matchingLineNumbers = findMatchingLineNumbers(canonFilename, searchPattern)
                if (matchingLineNumbers.size > 0) {
                    printMatchingLines(
                        canonFilename, matchingLineNumbers, searchPattern, linesBefore, linesAfter
                    )
                    numPatternMatches += 1
                }
            } catch {
                case t: Throwable => System.err.println(s"Throwable happened trying to read '$canonFilename'")
            }
        }
    }

    def done() = {
        println(s"Searched $numFilenameMatches '$filePattern' files, found $numPatternMatches matches.\n")
    }
    
    // invoke the filePattern matching method on each file
    override def visitFile(file: Path, attrs: BasicFileAttributes): FileVisitResult = {
        find(file)
        return CONTINUE
    }

    // invoke the filePattern matching method on each directory.
    // (ff: do nothing for directories)
    override def preVisitDirectory(dir: Path, attrs: BasicFileAttributes): FileVisitResult = {
        return CONTINUE
    }

    override def visitFileFailed(file: Path, ioe: IOException): FileVisitResult = {
        System.err.println(ioe)
        return CONTINUE
    }
    

}
