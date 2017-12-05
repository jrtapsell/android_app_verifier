package uk.co.jrtapsell.fyp.projectValidator

import java.io.File

/**
 * @author James Tapsell
 */

fun File.walkChildren(extension: String?, vararg specification: String?, block:(List<String>, File) -> Unit) {
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

fun forEachProject(block: (File) -> Unit) {
    File("../").walkChildren("gradle", null) { _, file ->
        if (file.name != "build.gradle") return@walkChildren
        block(file.parentFile)
    }
}