package uk.co.jrtapsell.fyp.baseUtils

/** Aliased to simplify changing this. */
typealias Intermediate = List<String>
/** Decodes a string. */
typealias Decoder = (String) -> Intermediate
/** Encodes a string. */
typealias Encoder = (Intermediate) -> String

/** Utility method to capitalise the first letter of a string. */
fun String.capitaliseFirst(): String {
    return this[0].toUpperCase() + this.substring(1, this.length)
}

private object CasesUtils {
    /** Regex for parsing upper camel case. */
    val UPPER_CAMEL_REGEX = Regex("""[A-Z][a-z]*""")
    /** Regex for parsing regular camel case. */
    val CAMEL_REGEX = Regex("""(?:[A-Z]?[a-z]+)|([A-Z])""")
    /** Decodes a string by spliting it by spaces. */
    val SPLIT_BY_SPACE: Decoder = { it.toLowerCase().split(" ") }
    /** Makes an encoder with the given parameters. */
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
    /** Decodes a string into groups based on the regex provided. */
    fun decodeByRegex(regex: Regex): Decoder {
        return { regex.findAll(it).map { it.value.toLowerCase() }.toList() }
    }
}

/** Different cases a string could be in. */
enum class Cases(val toList: Decoder, val fromList: Encoder) {
    /** Sentence case.
     *
     * Example:
     *
     * ```Hello world```
     */
    SENTENCE(
            CasesUtils.SPLIT_BY_SPACE,
            CasesUtils.makeEncoder(true, false, " ")),

    /** Upper sentence case.
     *
     * Example:
     *
     * ```Hello World```
     */
    UPPER_SENTENCE(
            CasesUtils.SPLIT_BY_SPACE,
            CasesUtils.makeEncoder(true, true, " ")
    ),

    /** Upper camel case.
     *
     * Example:
     *
     * ```HelloWorld```
     */
    UPPER_CAMEL(
            CasesUtils.decodeByRegex(CasesUtils.UPPER_CAMEL_REGEX),
            CasesUtils.makeEncoder(true, true, "")
    ),

    /** Camel case.
     *
     * Example:
     *
     * ```helloWorld```
     */
    CAMEL(
            CasesUtils.decodeByRegex(CasesUtils.CAMEL_REGEX),
            CasesUtils.makeEncoder(false, true, "")
    );

    /** Decodes the given string with this format. */
    fun decode(string: String) = toList(string)
    /** Decodes the given intermediate into a string with this format. */
    fun encode(input: Intermediate) = fromList(input)

    companion object {
        /** Converts from one format to another. */
        fun convert(input: Cases, output: Cases, text: String): String {
            val decoded = input.decode(text)
            return output.encode(decoded)
        }
    }
}