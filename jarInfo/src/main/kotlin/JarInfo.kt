import java.io.InputStream
import java.security.CodeSigner
import java.security.KeyStore
import java.security.cert.X509Certificate
import java.util.jar.JarEntry
import java.util.jar.JarFile
import javax.net.ssl.TrustManagerFactory
import javax.net.ssl.X509TrustManager

data class Record(val entry: JarEntry, val path: List<String>, val getContents: ()-> InputStream) {
    override fun toString(): String {
        return "Record(${entry.name})"
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

    fun isSigned(): Boolean {
        val signers = walk().filter { (entry, _, _) ->
            !entry.isDirectory
        }.map<Record, Array<CodeSigner>> { (entry, _, getContents) ->
            val contents = getContents()
            while (contents.read() != -1);
            entry.codeSigners?: arrayOf()
        }.toList()

        return signers.any { it.isEmpty() }
    }

    fun validate(cert: Array<out X509Certificate>) {
        val trustManagerFactory =
        TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm())
        val ks: KeyStore? = null
        trustManagerFactory.init(ks)

        for (trustManager in trustManagerFactory.trustManagers) {
            if (trustManager is X509TrustManager) {
                trustManager.checkServerTrusted(cert,"RSA")
            }
        }
    }
}