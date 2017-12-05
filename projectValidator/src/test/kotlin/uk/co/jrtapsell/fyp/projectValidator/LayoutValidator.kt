package uk.co.jrtapsell.fyp.projectValidator

import org.testng.annotations.Test
import uk.co.jrtapsell.fyp.baseUtils.testUtils.fail
import java.io.File

/** Validates the project. */
class LayoutValidator {

    /** Checks all the packages are correct. */
    @Test
    fun `Checks that kotlin files are in the right package`() {
        File("../").walkChildren("kt", null, "src", null, "kotlin" ) { name, file ->
            if (name[4] != "uk") throw AssertionError("uk check failed $file")
            if (name[5] != "co") throw AssertionError("co check failed $file")
            if (name[6] != "jrtapsell") throw AssertionError("jrtapsell check failed $file")
            if (name[7] != "fyp") throw AssertionError("fyp check failed $file")
            if (name[8] != name[0]) throw AssertionError("project name check failed $file")
        }
    }

    /** Checks that all modules have a testng.xml file. */
    @Test
    fun `Checks all projects have testng setup`() {
        forEachProject { directory ->
            val expectedTestFile = directory
                .resolve("src")
                .resolve("test")
                .resolve("testng.xml")
            if (!expectedTestFile.exists()) {
                throw AssertionError("${directory.name} has no testng.xml")
            }
        }
    }

    /** Checks I haven't added java files. */
    @Test
    fun `Check there are no java files in the project`() {
        forEachProject {
            val mainJavaDir = it.resolve("src").resolve("main").resolve("java")
            if (mainJavaDir.exists() && mainJavaDir.list().isNotEmpty()) {
                throw AssertionError("${it.name} contains main java code")
            }
            val testJavaDir = it.resolve("src").resolve("test").resolve("java")
            if (testJavaDir.exists() && testJavaDir.list().isNotEmpty()) {
                throw AssertionError("${it.name} contains test java code")
            }
        }
    }
}