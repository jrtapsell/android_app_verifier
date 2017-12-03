package uk.co.jrtapsell.fyp.jarInfo

import java.io.InputStream
import java.security.CodeSigner
import java.security.cert.X509Certificate
import java.util.*
import java.util.jar.JarEntry
import java.util.jar.JarFile

/** Represents a record in a JAR file. */
data class Record(val entry: JarEntry, val path: List<String>, val getContents: ()-> InputStream) {
    /** Represents this object as a string. */
    override fun toString(): String {
        return "uk.co.jrtapsell.jarInfo.Record(${entry.name})"
    }
}

/** Gives information about the JAR at the given filepath. */
class JarInfo(private val filePath: String) {

    private fun walk(): Sequence<Record> {
        val jarFile = JarFile(filePath)
        return jarFile.entries().asSequence().map { jarEntry ->
            Record(
                    entry = jarEntry,
                    path = jarEntry.name.split("/"),
                    getContents = { jarFile.getInputStream(jarEntry) }
            )
        }
    }

    /** Walks over all the files in the jar. */
    fun walkFiles(): Sequence<Record> {
        return walk().filter { (entry, _, _) -> !entry.isDirectory }
    }

    /** Walks over the files in a given directory. */
    fun listDirectory(vararg path: String): Sequence<Record> {
        return walk().filter { (_, item, _) ->
            if (path.size > item.size){
                false
            } else {
                item.subList(0, path.size) == path.toList()
            }
        }
    }

    private val noSign = Regex("""META-INF/[A-Z]+\.(RSA|DSA|SF)""")

    /** Gets the signers who have signed every entry that needs signing in the JAR file.
     *
     * This excludes the signature file, and also the directories.
     */
    fun getTotalSigners(): List<List<X509Certificate>> {
        val signers = walk().filter { (entry, _, _) ->
            !entry.isDirectory && !noSign.matches(entry.name)
        }.map { (entry, _, getContents) ->
            val contents = getContents()
            while (contents.read() != -1);
            entry.name to (entry.codeSigners?.toList()?: listOf())
        }.toList()

        fun X509Certificate.hexify() =
            this.publicKey.encoded.joinToString(" ") { it.toString(16) }

        if (signers.isEmpty()) return listOf()
        val cover = filterValid(signers[0]).map { it.hexify() }.toMutableList()

        val certs = mutableMapOf<String, List<X509Certificate>>()
        signers.forEach {
            it.second
                .forEach {
                val certChain = it.signerCertPath.certificates
                    .filter { it is X509Certificate }
                    .map { it as X509Certificate }
                val head = certChain[0].hexify()
                certs[head] = certChain
            }
            val items = filterValid(it).map { it.hexify() }
            cover.removeIf { !items.contains(it) }
        }
        return cover.mapNotNull(certs::get)
    }

    private fun filterValid(it: Pair<String, List<CodeSigner>>): MutableList<X509Certificate> {
        val signers = it.second
        val valid = signers
            .mapNotNull { it.signerCertPath.certificates[0] as? X509Certificate }
            .filter { it.checkCanCodeSign() }
        return valid.toMutableList()
    }

    private fun X509Certificate.checkCanCodeSign(): Boolean {
        val extendedKeyUsage = extendedKeyUsage ?: return false
        val signer = "1.3.6.1.5.5.7.3.3" in extendedKeyUsage
        val current = notAfter.after(Date()) && notBefore.before(Date())
        return signer && current
    }
}