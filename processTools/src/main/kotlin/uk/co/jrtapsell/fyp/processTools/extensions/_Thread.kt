package uk.co.jrtapsell.fyp.processTools.extensions

/** Adds extension methods for thread management. */
object _Thread {
    /**
     * Similar to the thread method in kotlin, except with a few extra features
     *
     * @param group
     *     The group to make the thread a member of
     * @param name
     *     The name of the thread to make
     * @param block
     *     The code to run
     */
    fun thread(group: ThreadGroup, name: String, block:()->Unit): Thread {
        val thread = Thread(group, block, name)
        thread.start()
        return thread
    }
}