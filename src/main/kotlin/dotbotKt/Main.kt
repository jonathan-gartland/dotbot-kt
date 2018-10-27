package dotbotKt

import com.github.ajalt.clikt.core.CliktCommand
/* ktlint-disable no-wildcard-imports */
import com.github.ajalt.clikt.parameters.options.*

data class DotBotKt(val baseDir: String?, val configFile: String?, val superQuiet: Boolean=false,
                    val quiet: Boolean=false, val verbose: Boolean=false,
                    val plugin: String?,
                    val pluginDir: String?,
                    val disableBuiltInPlugins: Boolean=false,
                    val noColor: Boolean=false)

class Cli : CliktCommand(
        help = """
            Dotbot is a tool that bootstraps your dotfiles (it's a [Dot]files [bo]o[t]strapper, get it?). It does
            less than you think, because version control systems do more than you think.

            Dotbot is designed to be lightweight and self-contained, with no external dependencies and no installation
            required. Dotbot can also be a drop-in replacement for any other tool you were using to manage your dotfiles,
            and Dotbot is VCS-agnostic -- it doesn't make any attempt to manage your dotfiles.
        """.trimIndent()) {
    init {
        versionOption("0.0.1")
    }

    private val _basedir  = "BASEDIR";
    private val _plugin = "PLUGIN";
    private val _pluginDir = "PLUGIN_DIR";

    private val baseDir: String by option("--base-directory", "-b",
            help = "execute commands from within").default(this._basedir)

    private val superQuiet: Boolean by option("--super-quiet", "-Q",
            help ="suppress almost all output").flag()

    private val quiet: Boolean by option("--quiet", "-q",
            help = "suppress most output").flag()

    private val verbose: Boolean by option("--verbose", "-v",
            help = "enable verbose output").flag()

    private val noColor: Boolean by option("--no-color",
            help = "disable color output").flag()

    private val configFile: String? by option("--config-file", "-c",
            help = "run commands given in CONFIGFILE")

    private val plugin: String by option("--plugins", "-p",
            help = "load PLUGIN as a plugin").default(this._plugin);

    private val pluginDir: String by option("--plugin-dir",
            help = "load all plugins in PLUGIN_DIR").default(this._pluginDir);

    private val disableBuiltInPlugins: Boolean by option("--disable-built-in-plugins",
            help = "disable built-in plugins").flag()

    override fun run() {
        val dotBotKt = DotBotKt(baseDir, configFile,
                superQuiet, quiet, verbose,
                plugin, pluginDir, disableBuiltInPlugins,
                noColor)
        context.obj = dotBotKt
    }
}

fun main(args: Array<String>) = Cli().main(args)
