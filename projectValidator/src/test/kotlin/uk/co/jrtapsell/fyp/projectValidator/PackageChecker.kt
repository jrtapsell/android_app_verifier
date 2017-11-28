package uk.co.jrtapsell.fyp.projectValidator

import org.testng.annotations.Test
import java.io.File

class PackageChecker {
    private fun File.walkChildren(extension: String?, vararg specification: String?, block:(List<String>, File) -> Unit) {
        this.walk().onEnter {
            it.name != "gitRepos" // Don't go deeper into repos forever
        }.forEach {
            val relative = it.relativeTo(this)
            if (it.isDirectory) return@forEach
            if (extension != null && it.extension != extension) return@forEach
            val segments = relative.toString().split("/")
            if (segments.size <= specification.size) return@forEach
            for (i in specification.indices) {
                val specValue = specification[i] ?: continue
                val segValue = segments[i]
                if (specValue != segValue) return@forEach
            }
            block(segments, it)
        }

    }

    @Test
    fun checkFolders() {
        File("../").walkChildren("kt", null, "src", null, "kotlin" ) { name, file ->
            if (name[4] != "uk") throw AssertionError("uk check failed $file")
            if (name[5] != "co") throw AssertionError("co check failed $file")
            if (name[6] != "jrtapsell") throw AssertionError("jrtapsell check failed $file")
            if (name[7] != "fyp") throw AssertionError("fyp check failed $file")
            if (name[8] != name[0]) throw AssertionError("project name check failed $file")
        }
    }

    @Test
    fun checkHeaders() {
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
    @Test
    fun checkHasTests() {
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

    @Test
    fun checkHasNoJava() {
        forEachProject {
            val mainJavaDir = it.resolve("src").resolve("main").resolve("java")
            if (mainJavaDir.exists() and mainJavaDir.list().isNotEmpty()) {
                throw AssertionError("${it.name} contains main java code")
            }
            val testJavaDir = it.resolve("src").resolve("test").resolve("java")
            if (testJavaDir.exists() and testJavaDir.list().isNotEmpty()) {
                throw AssertionError("${it.name} contains test java code")
            }
        }
    }
}