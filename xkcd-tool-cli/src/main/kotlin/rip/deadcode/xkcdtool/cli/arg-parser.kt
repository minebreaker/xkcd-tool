package rip.deadcode.xkcdtool.cli


data class Config(
    val name: List<String>,
    val urlMode: Boolean,
    val explainMode: Boolean
)

fun parseArg(jvmArg: Array<String>): Config {

    fun parse(current: Config, tokens: List<String>): Pair<Config, List<String>> {
        return if (tokens.isEmpty()) {
            current to listOf()
        } else {
            val token = tokens.first()
            val rest = tokens.subList(1, tokens.size)

            val nextConfig = when (token) {
                "--url", "l" -> current.copy(urlMode = true)
                "--explain", "e" -> current.copy(urlMode = true)
                else -> current.copy(name = current.name + token)
            }

            parse(nextConfig, rest)
        }

    }

    val defaultConfig = Config(listOf(), false, false)
    val (config) = parse(defaultConfig, jvmArg.asList())
    return config
}
