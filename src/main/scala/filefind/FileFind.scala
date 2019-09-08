import java.io._
import java.nio.file._
import java.nio.file.attribute._
import java.util._
import java.nio.file.FileVisitResult._  //static
import java.nio.file.FileVisitOption._  //static
import scopt.OParser

object FileFind extends App {

    // MAIN
    //if (args.length < 3 || !args(1).equals("-name")) usage()
    
    case class Config(
        searchDir: String = "",         //search dir
        searchPattern: String = "",     //filePattern to search for
        filenamePattern: String = ""    //filename filePattern to search for
    )

    val builder = OParser.builder[Config]
    val parser1: OParser[Unit,Config] = {
      import builder._
      OParser.sequence(
        programName("ff"),
        head("ff", "0.1"),
        opt[String]('d', "dir")
            .required()
            .action((x, c) => c.copy(searchDir = x))
            .text("required"),
        opt[String]('p', "search-filePattern")
            .required()
            .valueName("[searchPattern] (like 'StringBuilder')")
            .action((x, c) => c.copy(searchPattern = x))
            .text("required"),
        opt[String]('f', "filename-filePattern")
            .required()
            .valueName("[filenamePattern] (like '*.java')")
            .action((x, c) => c.copy(filenamePattern = x))
            .text("required"),
      )
    }

    OParser.parse(parser1, args, Config()) match {
        case Some(config) =>
          doTheSearch(config)
        case _ =>
          // arguments are bad, error message will have been displayed
    }

    //def doTheSearch(parser: OParser[Unit,Config]) = {
    def doTheSearch(config: Config) = {
        //val startingDir: Path = Paths.get(args(0))
        //val filePattern = args(2)   // "*java"

        val startingDir = config.searchDir
        val searchPattern = config.searchPattern
        val filenamePattern = config.filenamePattern
    
        val finder = new Finder(filenamePattern, searchPattern)
        Files.walkFileTree(Paths.get(startingDir), finder)
        finder.done()
    
        // def usage(): Unit = {
        //     System.err.println("java Find <path>" + " -name \"<glob_pattern>\"")
        //     System.exit(-1)
        // }
    }


}


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
        // don’t do anything with directories
        //if (file.toFile().isDirectory()) return;
        val name: Path = file.getFileName()
        if (name != null && matcher.matches(name)) {
            numMatches += 1
            println(s"FILE: $file")
        }
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



