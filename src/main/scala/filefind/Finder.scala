package filefind

import java.io._
import java.nio.file._
import java.nio.file.attribute._
import java.util._
import java.nio.file.FileVisitResult._  //static
import java.nio.file.FileVisitOption._  //static

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
            if (fileContainsPattern(canonFilename, searchPattern)) {
                println(s"MATCHED $file")
            }
        }

        /**
         * What I Really Want:
         *     - if the file matches the pattern
         *     - find all lines in the file that match (matchingLineNumbers:List[Int])
         *     - go back through the file and get the before and after lines
         *       as well as the desired line
         */
    }

    private def fileContainsPattern(filename: String, pattern: String): Boolean = {
        var containsPattern = false

        // println(s"searching $filename ...")
        val br = new BufferedReader(new FileReader(filename))
        var line = ""
        while ({line = br.readLine; line != null}) {
            if (line.contains(pattern)) {
                containsPattern = true
                //println(s"MATCHED $filename")
            } 
        }
        br.close

        containsPattern
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
