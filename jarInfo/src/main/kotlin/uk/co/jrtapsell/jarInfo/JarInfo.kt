package uk.co.jrtapsell.jarInfo

import java.io.InputStream
import java.security.CodeSigner
import java.security.cert.X509Certificate
import java.util.*
import java.util.jar.JarEntry
import java.util.jar.JarFile

data class Record(val entry: JarEntry, val path: List<String>, val getContents: ()-> InputStream) {
    override fun toString(): String {
        return "uk.co.jrtapsell.jarInfo.Record(${entry.name})"
    }
}
class JarInfo(val filePath: String) {
    fun walk(): Sequence<Record> {
        val jarFile = JarFile(filePath)
        return jarFile.entries().asSequence().map { jarEntry ->
            Record(
                    entry = jarEntry,
                    path = jarEntry.name.split("/"),
                    getContents = { jarFile.getInputStream(jarEntry) }
            )
        }
    }
    fun walkFiles(): Sequence<Record> {
        return walk().filter { (entry, _, _) -> !entry.isDirectory }
    }

    fun listDirectory(vararg path: String): Sequence<Record> {
        return walk().filter { (_, item, _) ->
            if (path.size > item.size){
                false
            } else {
                item.subList(0, path.size) == path.toList()
            }
        }
    }

    val noSign = Regex("""META-INF/[A-Z]+\.(RSA|DSA|SF)""")

    fun isSigned(): Boolean {
        val signers = walk().filter { (entry, _, _) ->
            !entry.isDirectory && !noSign.matches(entry.name)
        }.map { (entry, _, getContents) ->
            val contents = getContents()
            while (contents.read() != -1);
            entry.name to (entry.codeSigners?.toList()?: listOf())
        }.toList()

        return signers.all { hasValid(it) }
    }

    private fun hasValid(it: Pair<String, List<CodeSigner>>): Boolean {
        val signers = it.second
        val valid = signers.any {
            val head = it.signerCertPath.certificates[0] as? X509Certificate ?: return false
            val signer = "1.3.6.1.5.5.7.3.3" in (head.extendedKeyUsage?:return false)
            val current = head.notAfter.after(Date()) && head.notBefore.before(Date())
            return signer && current
        }
        return valid
    }
}