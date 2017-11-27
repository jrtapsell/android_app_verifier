package uk.co.jrtapsell.jarInjector

import uk.co.jrtapsell.jarInfo.JarInfo
import java.io.*
import java.util.jar.JarEntry
import java.util.jar.JarInputStream
import java.util.jar.JarOutputStream

import uk.co.jrtapsell.gitWrapper.processIO.run

class JarManipulator(val filename: String) {

    fun withJarStreams(newFileName: String, block:(JarInputStream, JarOutputStream) -> Unit): JarManipulator {
        JarInputStream(FileInputStream(filename)).use { input ->
            JarOutputStream(FileOutputStream(newFileName)).use { output ->
                block(input, output)
                output.flush()
            }
        }
        return JarManipulator(newFileName)
    }

    val info: JarInfo
        get() = JarInfo(filename)

    fun inject(
        outputJarfile: String,
        fileName: String,
        fileContents: String): JarManipulator {
        if (info.getTotalSigners().isNotEmpty()) {
            throw AssertionError("Injecting would break the signature, please unsign first")
        }
        return withJarStreams(outputJarfile) { input, output ->
            cloneManifest(output, input)
            generateSequence { input.nextJarEntry }.forEach { jarEntry ->
                cloneFile(output, jarEntry, input)
            }
            output.putNextEntry(JarEntry(fileName))
            val writer = output.writer()
            writer.write(fileContents)
            writer.flush()
        }
    }

    fun sign(outputJarfile: String): JarManipulator {

        val parts = arrayOf(
                "jarsigner",
                "-keystore",
                "../keys/mock",
                "-signedJar",
                outputJarfile,
                "-keypass",
                "password",
                "-storepass",
                "password",
                filename,
                "mock"
        )
        val process = run(true, "./", *parts)
        process.use {  }
        process.assertClosedCleanly()
        return JarManipulator(outputJarfile)
    }

    fun unsign(outputJarfile: String): JarManipulator {

        fun copyIfNotSignature(input: JarInputStream, output: JarOutputStream, entry: JarEntry) {
            with(entry.name) {
                when {
                    startsWith("META-INF/") && endsWith(".SF") -> return
                    startsWith("META-INF/") && endsWith(".RSA") -> return
                    startsWith("META-INF/") && endsWith(".DSA") -> return
                    else -> {
                        cloneFile(output, entry, input)
                    }
                }
            }
        }

        return withJarStreams(outputJarfile) { input, output ->
            generateSequence { input.nextJarEntry }.forEach { entry ->
                copyIfNotSignature(input, output, entry)
            }


            cloneManifest(output, input)
        }
    }

    private fun cloneManifest(output: JarOutputStream, input: JarInputStream) {
        output.putNextEntry(JarEntry("META-INF/MANIFEST.MF"))
        PrintStream(output).let { outputWriter ->
            input.manifest.mainAttributes.forEach { t ->
                outputWriter.println("${t.key}: ${t.value}")
            }
        }
    }
    private fun cloneFile(output: JarOutputStream, entry: JarEntry, input: JarInputStream) {
        output.putNextEntry(JarEntry(entry.name))
        var i = input.read()
        while (i != -1) {
            output.write(i)
            i = input.read()
        }
    }
}