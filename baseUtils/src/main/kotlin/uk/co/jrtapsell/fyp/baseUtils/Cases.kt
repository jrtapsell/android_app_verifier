package uk.co.jrtapsell.fyp.baseUtils

typealias Decoder = (String) -> List<String>
typealias Encoder = (List<String>) -> String

fun String.capitaliseFirst(): String {
    return this[0].toUpperCase() + this.substring(1, this.length)
}

private object CasesUtils {
    val UPPER_CAMEL_REGEX = Regex("""[A-Z][a-z]*""")
    val CAMEL_REGEX = Regex("""(?:[A-Z]?[a-z]+)|([A-Z])""")
    val SPLIT_BY_SPACE: Decoder = { it.toLowerCase().split(" ") }
    fun makeEncoder(capsFirst: Boolean, capsOther: Boolean, seperator: String): Encoder {
        return {
            it.mapIndexed { index, word ->
                if (index != 0) {
                    if (capsOther) word.capitaliseFirst() else word
                } else {
                    if (capsFirst) word.capitaliseFirst() else word
                }
            }.joinToString(seperator)
        }
    }
    fun decodeByRegex(regex: Regex): Decoder {
        return { regex.findAll(it).map { it.value.toLowerCase() }.toList() }
    }
}

enum class Cases(val toList: Decoder, val fromList: Encoder) {
    SENTANCE(
            CasesUtils.SPLIT_BY_SPACE,
            CasesUtils.makeEncoder(true, false, " ")),
    UPPER_SENTANCE(
            CasesUtils.SPLIT_BY_SPACE,
            CasesUtils.makeEncoder(true, true, " ")
    ),
    UPPER_CAMEL(
            CasesUtils.decodeByRegex(CasesUtils.UPPER_CAMEL_REGEX),
            CasesUtils.makeEncoder(true, true, "")
    ),
    CAMEL(
            CasesUtils.decodeByRegex(CasesUtils.CAMEL_REGEX),
            CasesUtils.makeEncoder(false, true, "")
    );

    fun decode(string: String) = toList(string)
    fun encode(input: List<String>) = fromList(input)

    companion object {
        fun convert(input: Cases, output: Cases, text: String): String {
            val decoded = input.toList(text)
            return output.fromList(decoded)
        }
    }
}