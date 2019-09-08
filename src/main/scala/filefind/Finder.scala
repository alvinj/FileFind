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
    var numMatches = 0

    // "glob:" is part of the syntax
    // https://docs.oracle.com/javase/8/docs/api/java/nio/file/FileSystem.html#getPathMatcher-java.lang.String-
    pathMatcher = FileSystems.getDefault()
        .getPathMatcher("glob:" + filePattern)

    // compares the glob filePattern against the file or directory name
    def find(file: Path): Unit = {
        val name: Path = file.getFileName()
        if (name != null && pathMatcher.matches(name)) {
            numMatches += 1
            val canonFilename = file.toAbsolutePath.toString
            val matchingLineNumbers = findMatchingLineNumbers(canonFilename, searchPattern)
            if (findMatchingLineNumbers(canonFilename, searchPattern).size > 0) {
                printMatchingLineNumbers(
                    canonFilename, matchingLineNumbers, searchPattern, linesBefore, linesAfter
                )
            }
        }
    }

    def done() = {
        println(s"Searched $numMatches $filePattern files.\n")
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
