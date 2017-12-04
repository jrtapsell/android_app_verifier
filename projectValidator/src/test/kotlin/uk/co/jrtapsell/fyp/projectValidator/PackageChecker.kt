package uk.co.jrtapsell.fyp.projectValidator

import org.testng.Assert
import org.testng.annotations.Test
import java.io.File

/** Validates the project. */
class PackageChecker {
    private fun File.walkChildren(extension: String?, vararg specification: String?, block:(List<String>, File) -> Unit) {
        val localFiles = this.walk().onEnter {
            it.name != "gitRepos" // Don't go deeper into repos forever
        }
        for (it in localFiles) {
            val relative = it.relativeTo(this)
            if (it.isDirectory) continue
            if (extension != null && it.extension != extension) continue
            val segments = relative.toString().split("/")
            if (segments.size <= specification.size) continue
            for (i in specification.indices) {
                val specValue = specification[i] ?: continue
                val segValue = segments[i]
                if (specValue != segValue) continue
            }
            block(segments, it)
        }

    }

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

    /** Checks the package lines are present.
     *
     * This is needed because kotlin allows files in a different place to their package.
     */
    @Test
    fun `Checks the package line of files is correct`() {
        val headerRegex = Regex("""package ([A-z]+(?:\.[A-z]+)+)""")
        File("../").walkChildren("kt", null, "src", null, "kotlin" ) { name, file ->
            val header = file.bufferedReader().use { it.readLine() }!!
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

    private fun forEachProject(block: (File) -> Unit) {
        File("../").walkChildren("gradle", null) { _, file ->
            if (file.name != "build.gradle") return@walkChildren
            block(file.parentFile)
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

    /** Checks that no test have Java style names. */
    @Test
    fun `Check test names are using the descriptive form`() {
        val testRegex = Regex("""@Test(\([^)]+\))?\s+fun ([^(]+)""")
        val noDataProvider = Regex("""`[A-Z][a-z]+( [a-z0-9A-Z\-']+,?){3,}`""")
        val dataProvider = Regex("""`[A-Z][a-z]+( [a-z0-9A-Z\-']+,?){3,} `""")
        File("../").walkChildren("kt", null, "src", "test") { path, file ->
            val text = file.readText()
            testRegex.findAll(text).forEach {
                val annotationParams = it.groups[1]?.value
                val (targetRegex, nameGroup) = if (annotationParams?.contains("dataProvider") == true) {
                    dataProvider to it.groups[2]
                }
                else {
                    noDataProvider to it.groups[2]
                }
                val name = nameGroup!!.value
                if (targetRegex.matchEntire(name) == null) {
                    Assert.fail("""
                            |Name of {$path | $name} is wrong:
                            |Expected: ${targetRegex.pattern}
                            |Actual  : $name
                            |Based on: $annotationParams
                    """.trimMargin())
                }
            }
        }
    }
}