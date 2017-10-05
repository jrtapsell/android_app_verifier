package uk.co.jrtapsell.gitWrapper

import org.testng.Assert
import org.testng.annotations.Test
import uk.co.jrtapsell.gitWrapper.processIO.run

/** Using https://www.tengio.com/blog/more-readable-tests-with-kotlin/ */
class TestProcessIO {

    @Test
    fun `Runs ls as a basic process test`() {
        val run = run(true, "/", "ls")
        run.use {
            it.forEach {
                println(it)
            }
        }
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
            val pro = run(false,"/", "sleep", "10")
            pro.use {}
        }
    }

    @Test
    fun `Checks test script exit code is returned`() {
        val pro = run(
                true,
                PROJECT_DIR +"/gitWrapper/scripts/",
                "bash",
                "exit.sh",
                "1")
        pro.use{}

        Assert.assertEquals(pro.exitCode, 1, "Wrong exit code")
    }
}