package uk.co.jrtapsell.fyp.baseUtils.testUtils

import org.testng.annotations.DataProvider
import org.testng.annotations.Test
import uk.co.jrtapsell.fyp.baseUtils.Cases

/** Tests the case utilities. */
class CaseTest {

    /** Hello World for basic tests. */
    val HELLO_WORLDS = mapOf(
            Cases.SENTENCE to "Hello world",
            Cases.UPPER_SENTENCE to "Hello World",
            Cases.CAMEL to "helloWorld",
            Cases.UPPER_CAMEL to "HelloWorld"
    ).entries

    /** Single letter words break a basic convertor. */
    val DIFFICULT = mapOf(
            Cases.SENTENCE to "I am a difficult sentence because i contain a lot of i",
            Cases.UPPER_SENTENCE to "I Am A Difficult Sentence Because I Contain A Lot Of I",
            Cases.CAMEL to "iAmADifficultSentenceBecauseIContainALotOfI",
            Cases.UPPER_CAMEL to "IAmADifficultSentenceBecauseIContainALotOfI"
    ).entries

    /** Data provider for the test words. */
    @DataProvider(name="words")
    fun dataProvider(): Array<Array<Any>> {
        val helloWorlds = dataise(HELLO_WORLDS)
        val difficults = dataise(DIFFICULT)
        return (helloWorlds + difficults).toTypedArray()
    }

    /** Converts a map to DataProvider format. */
    private fun dataise(dataset: Set<Map.Entry<Cases, String>>): List<Array<Any>> {
        return dataset.flatMap { outer -> dataset.map { it to outer } }
            .map { (start, end) ->
                val (startCase, startText) = start
                val (endCase, endText) = end
                arrayOf(startCase, endCase, startText, endText)
            }
    }

    /** Runs the tests. */
    @Test(dataProvider = "words")
    fun testsSimpleConversions(startCase: Cases, endCase: Cases, startText: String, endText: String) {
        Cases.convert(startCase, endCase, startText) assertEquals endText
    }
}