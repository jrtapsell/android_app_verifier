package uk.co.jrtapsell.fyp.projectValidator

import org.testng.annotations.Test
import uk.co.jrtapsell.fyp.baseUtils.testUtils.fail
import java.io.File

/**
 * @author James Tapsell
 */
class KotlinValidator {


    /** Checks the package lines are present.
     *
     * This is needed because kotlin allows files in a different place to their package.
     */
    @Test
    fun `Checks the package line of files is correct`() {
        val headerRegex = Regex("""package ([A-z]+(?:\.[A-z]+)+)""")
        File("../").walkChildren("kt", null, "src", null, "kotlin" ) { name, file ->
            val header = file.bufferedReader().use { it.readLine() } ?: fail("File missing package")
            val match = headerRegex.matchEntire(header)?:throw AssertionError(header)
            val packageName = match.groupValues[1]
            val segments = packageName.split(".")
            val folderPackage = name.subList(4, name.size - 1)
            val folderPackageString = folderPackage.joinToString(".")
            if (folderPackage.size != segments.size)
                throw AssertionError("$packageName != $folderPackageString")
            folderPackage.zip(segments).forEach { (folderSection, headerSection) ->
                if (folderSection != headerSection) {
                    throw AssertionError("$packageName != $folderPackageString")
                }
            }
        }
    }

    /** Checks that no test have Java style names. */
    @Test
    fun `Check test names are using the descriptive form`() {
        val testRegex = Regex("""@Test(\([^)]+\))?\s+fun ([^(]+)""")
        val noDataProvider = Regex("""`[A-Z][a-z]+( [a-z0-9A-Z\-']+,?){3,}`""")
        val dataProvider = Regex("""`[A-Z][a-z]+( [a-z0-9A-Z\-']+,?){3,} `""")
        File("../").walkChildren("kt", null, "src", "test") { path, file ->
            val text = file.readText()
            testRegex.findAll(text).forEach {
                val (_, testParams, testName) = it.groupValues
                val targetRegex = if (testParams.contains("dataProvider")) dataProvider else noDataProvider
                if (targetRegex.matchEntire(testName) == null) {
                    fail("""
                            |Name of {$path | $testName} is wrong:
                            |Expected: ${targetRegex.pattern}
                            |Actual  : $testName
                            |Based on: $testParams
                    """.trimMargin())
                }
            }
        }
    }
}