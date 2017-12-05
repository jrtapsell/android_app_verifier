package uk.co.jrtapsell.fyp.baseUtils.testUtils

import org.testng.annotations.DataProvider
import org.testng.annotations.Test
import uk.co.jrtapsell.fyp.baseUtils.Cases

class CaseTest {

    val HELLO_WORLDS = mapOf(
            Cases.SENTANCE to "Hello world",
            Cases.UPPER_SENTANCE to "Hello World",
            Cases.CAMEL to "helloWorld",
            Cases.UPPER_CAMEL to "HelloWorld"
    ).entries

    val DIFFICULT = mapOf(
            Cases.SENTANCE to "I am a difficult sentence because i contain a lot of i",
            Cases.UPPER_SENTANCE to "I Am A Difficult Sentence Because I Contain A Lot Of I",
            Cases.CAMEL to "iAmADifficultSentenceBecauseIContainALotOfI",
            Cases.UPPER_CAMEL to "IAmADifficultSentenceBecauseIContainALotOfI"
    ).entries

    @DataProvider(name="helloWorld")
    fun dataProvider(): Array<Array<Any>> {
        val helloWorlds = dataise(HELLO_WORLDS)
        val difficults = dataise(DIFFICULT)
        return (helloWorlds + difficults).toTypedArray()
    }

    private fun dataise(dataset: Set<Map.Entry<Cases, String>>): List<Array<Any>> {
        return dataset.flatMap { outer -> dataset.map { it to outer } }
            .map { (start, end) ->
                val (startCase, startText) = start
                val (endCase, endText) = end
                arrayOf(startCase, endCase, startText, endText)
            }
    }

    @Test(dataProvider = "helloWorld")
    fun testsSimpleConversions(startCase: Cases, endCase: Cases, startText: String, endText: String) {
        Cases.convert(startCase, endCase, startText) assertEquals endText
    }
}