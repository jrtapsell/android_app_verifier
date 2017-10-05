package uk.co.jrtapsell.gitWrapper

import org.testng.Assert

/**
 * @author James Tapsell
 */
fun timeLimit(minimumMS: Long, maximumMS: Long, block: () -> Unit) {
    val start = System.currentTimeMillis()
    block.invoke()
    val end = System.currentTimeMillis()
    val delta = end - start
    Assert.assertTrue(delta >= minimumMS, "Block took too little time $delta")
    Assert.assertTrue(delta <= maximumMS, "Block took too much time $delta")
}