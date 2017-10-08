package extensions

object _Thread {
    fun thread(group: ThreadGroup, name: String, block:()->Unit): Thread {
        val thread = Thread(group, block, name)
        thread.start()
        return thread
    }
}