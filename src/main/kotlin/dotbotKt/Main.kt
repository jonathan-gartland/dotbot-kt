package dotbotKt

import com.github.ajalt.clikt.core.CliktCommand
/* ktlint-disable no-wildcard-imports */
import com.github.ajalt.clikt.parameters.options.*

data class DotBotKt(val baseDir: String?, val configFile: String?, val superQuiet: Boolean=false,
                    val quiet: Boolean=false, val verbose: Boolean=false)

class Cli : CliktCommand(
        help = """
            test.
        """.trimIndent()) {
    init {
        versionOption("0.0")
    }

    val baseDir: String by option("--base-directory", "-b",
            help = "execute commands from within").default("BASEDIR")
    val configFile: String? by option("--config-file", "-c",
            help = "run commands given in CONFIGFILE")
    val superQuiet: Boolean by option("--super-quiet", "-Q",
            help ="suppress almost all output").flag()
    val quiet: Boolean by option("--quiet", "-q",
            help = "suppress most output").flag()
    val verbose: Boolean by option("--verbose", "-v",
            help = "enable verbose output").flag()

    override fun run() {
        val dotBotKt = DotBotKt(baseDir, configFile, superQuiet, quiet, verbose)
        context.obj = dotBotKt
    }
}

fun main(args: Array<String>) = Cli().main(args)
