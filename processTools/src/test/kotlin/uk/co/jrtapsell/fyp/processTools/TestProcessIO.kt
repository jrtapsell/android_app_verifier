package uk.co.jrtapsell.fyp.processTools

import org.testng.Assert
import org.testng.annotations.Test
import java.io.File

/** Using https://www.tengio.com/blog/more-readable-tests-with-kotlin/ */
class TestProcessIO {

    private fun timeLimit(minimumMS: Long, maximumMS: Long, block: () -> Unit) {
        val start = System.currentTimeMillis()
        block.invoke()
        val end = System.currentTimeMillis()
        val delta = end - start
        Assert.assertTrue(delta >= minimumMS, "Block took too little time $delta")
        Assert.assertTrue(delta <= maximumMS, "Block took too much time $delta")
    }

    @Test
    fun `Runs ls as a basic process test`() {
        val lines = mutableListOf<Line>()
        val run = run(true, "/", "ls")
        run.use { it.forEach { lines.add(it) } }
        Assert.assertNotEquals(lines.size, 0, "No items")
    }

    @Test
    fun `Checks it waits for the process if asked`() {
        timeLimit(900, 1200) {
            val pro = run(true, "/", "sleep", "1")
            pro.use {}
        }
    }

    @Test
    fun `Checks processes that keep running are killed`() {
        timeLimit(0, 200) {
            val pro = run(false, "/", "sleep", "10")
            pro.use {}
        }
    }

    @Test
    fun `Checks test script exit code is returned`() {
        println(File("").absoluteFile.toString())
        val pro = run(
                true,
                "scripts/",
                "bash",
                "exit.sh",
                "1")
        pro.use{}

        Assert.assertEquals(pro.exitCode, 1, "Wrong exit code")
    }

    @Test
    fun `Tests input can be read back`() {
        val pro = run(true, "/", "cat", "-")
        pro.inputLine("Hello World")
        pro.closeInput()
        val output = pro.map { it.text }.toList()
        Assert.assertEquals(output, listOf("Hello World"))
    }

    @Test(expectedExceptions = arrayOf(AssertionError::class))
    fun `Tests unclean close fails assertion`() {
        val pro = run(true, "scripts/", "bash", "exit.sh", "1")
        pro.use {  }
        pro.assertClosedCleanly()
    }

    @Test
    fun `Tests clean close passes assertion`() {
        val pro = run(true, "scripts/", "bash", "exit.sh", "0")
        pro.use {  }
        pro.assertClosedCleanly()
    }
}