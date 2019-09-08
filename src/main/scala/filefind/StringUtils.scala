package filefind

object StringUtils {

    /**
     * Highlights the search pattern within the string `line`.
     * It currently makes the search pattern bold and underlined.
     * The output string is intended to be shown in an ANSI terminal,
     * and I can confirm that it works with the MacOS Terminal.
     * 
     * More info on escape sequences: 
     * stackoverflow.com/questions/4842424/list-of-ansi-color-escape-sequences
     */
    def highlightSearchPatternForAnsiTerminals(line: String, theSearchPattern: String): String =
        line.replaceAll(
            theSearchPattern,
            s"\033[1;4m${theSearchPattern}\033[0m"
        )

    /**
     * Create an “underline” string that’s the same length as the input string.
     * {{{
     *     val s = "foobar"
     *     val u = makeUnderline(s)
     *     println(u)  // "------"
     * }}}
     */
    def makeUnderline(s: String) = List.fill(s.length)('-').mkString
    //def dash(s: String) = "-"*s.length  //via twitter

}