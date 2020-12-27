package rip.deadcode.xkcdtool.cli


data class Config(
    val query: List<String>,
    val urlMode: Boolean,
    val explainMode: Boolean,
    val imageMode: Boolean
)

fun parseArg(jvmArg: Array<String>): Config {

    fun parse(current: Config, tokens: List<String>): Pair<Config, List<String>> {
        return if (tokens.isEmpty()) {
            current to listOf()
        } else {
            val token = tokens.first()
            val rest = tokens.subList(1, tokens.size)

            val nextConfig = when (token) {
                "--url", "-l" -> current.copy(urlMode = true)
                "--explain", "-e" -> current.copy(explainMode = true)
                "--image", "-i" -> current.copy(imageMode = true)  // TODO
                else -> current.copy(query = current.query + token)
            }

            parse(nextConfig, rest)
        }

    }

    val defaultConfig = Config(listOf(), false, false, false)
    val (config) = parse(defaultConfig, jvmArg.asList())
    return config
}
