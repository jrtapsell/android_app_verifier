package uk.co.jrtapsell.fyp.projectValidator

import org.testng.annotations.DataProvider
import org.testng.annotations.Test
import org.w3c.dom.Element
import org.w3c.dom.NamedNodeMap
import org.w3c.dom.Node
import org.w3c.dom.NodeList
import uk.co.jrtapsell.fyp.baseUtils.Cases
import uk.co.jrtapsell.fyp.baseUtils.testUtils.assertEquals
import javax.xml.parsers.DocumentBuilderFactory

/** Validate the testng.xml files. */
class TestNgValidator {
    private val DOCUMENT_BUILDER = DocumentBuilderFactory.newInstance().newDocumentBuilder()

    @DataProvider(name = "configs")
    private fun configProvider(): Array<Array<Any>> {
        val items = mutableListOf<List<Any>>()
        forEachProject { item ->
            val testFile = item.resolve("src")
                .resolve("test")
                .resolve("testng.xml")
            val config = DOCUMENT_BUILDER.parse(testFile).documentElement
            if (config != null) {
                items.add(listOf(config, item.name))
            } else {
                println(item)
            }
        }
        return items.map { it.toTypedArray() }.toTypedArray()
    }

    /** Checks that all of the suite names are the upper sentence form of their module name. */
    @Test(dataProvider = "configs")
    fun `Validates the suite names`(config: Element, projectName: String) {
        config.classify() assertEquals NodeType.SUITE
        val name = config.attr("name")
        Cases.convert(Cases.UPPER_SENTENCE, Cases.CAMEL, name) assertEquals projectName
        Cases.convert(Cases.CAMEL, Cases.UPPER_SENTENCE, projectName) assertEquals name
    }

    /** Gets an attribute of the given node. */
    fun Node.attr(s: String): String {
        return checkNotNull(this.attributes.toMap().get(s))
    }

    /** Gets the type of the given node, or fails if unknown */
    fun Node.classify() = NodeType.classify(this)

    /** Gets the children of this node in a list to allow Kotlin methods. */
    fun Element.children() = childNodes?.children() ?: listOf()

    /** Converts a nodeList to a List. */
    fun NodeList.children() = (0 until length).map { item(it) }
        .filter { it.hasChildNodes() || !it.textContent.trim().isBlank()}

    /** Gets all of the attributes of the given node as a map. */
    fun NamedNodeMap?.toMap(): Map<String, String> {
        if (this == null) return mapOf()
        return (0 until length).associate {
            val node = item(it)
            node.nodeName to node.textContent
        }
    }

    /** The expected node types in a testng.xml file. */
    enum class NodeType(val nodeName: String) {
        /** An XML comment. */
        COMMENT("#comment"),
        /** A suite declaration. */
        SUITE("suite"),
        /** A test declaration. */
        TEST("test");

        companion object {
            /** Classifies the given node into one of the expected types, or fails. */
            fun classify(element: Node) =
                values().firstOrNull { it.nodeName == element.nodeName } ?:
                        throw AssertionError("Unknown type: ${element.nodeName}")
        }
    }
}
