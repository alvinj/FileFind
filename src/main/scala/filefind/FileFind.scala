package filefind

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
        filenamePattern: String = "",   //filename filePattern to search for
        before: Int = 0,
        after: Int = 0
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
        opt[String]('p', "search-pattern")
            .required()
            .valueName("[searchPattern] (like 'StringBuilder')")
            .action((x, c) => c.copy(searchPattern = x))
            .text("required"),
        opt[String]('f', "filename-pattern")
            .required()
            .valueName("[filenamePattern] (like '*.java')")
            .action((x, c) => c.copy(filenamePattern = x))
            .text("required"),
        opt[Int]('b', "before")
            .valueName("[before] (the number of lines BEFORE the search pattern to print, like 1 or 2)")
            .action((x, c) => c.copy(before = x)),
        opt[Int]('a', "after")
            .valueName("[after]   (the number of lines AFTER the search pattern to print, like 1 or 2)")
            .action((x, c) => c.copy(after = x))
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





