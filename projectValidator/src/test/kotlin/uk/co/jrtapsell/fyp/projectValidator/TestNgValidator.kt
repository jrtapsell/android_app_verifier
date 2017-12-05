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

class TestNgValidator {
    val DOCUMENT_BUILDER = DocumentBuilderFactory.newInstance().newDocumentBuilder()

    @DataProvider(name = "configs")
    fun configProvider(): Array<Array<Any>> {
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

    @Test(dataProvider = "configs")
    fun `Validates the suite names`(config: Element, projectName: String) {
        config.classify() assertEquals NodeType.SUITE
        val name = config.attr("name")
        Cases.convert(Cases.UPPER_SENTANCE, Cases.CAMEL, name) assertEquals projectName
        Cases.convert(Cases.CAMEL, Cases.UPPER_SENTANCE, projectName) assertEquals name
    }

    fun Node.attr(s: String): String {
        return checkNotNull(this.attributes.toMap().get(s))
    }

    fun Node.classify() = NodeType.classify(this)
    fun Element.children() = childNodes?.children() ?: listOf()
    fun NodeList.children() = (0 until length).map { item(it) }
        .filter { it.hasChildNodes() || !it.textContent.trim().isBlank()}

    fun NamedNodeMap?.toMap(): Map<String, String> {
        if (this == null) return mapOf()
        return (0 until length).associate {
            val node = item(it)
            node.nodeName to node.textContent
        }
    }

    enum class NodeType(val nodeName: String) {
        COMMENT("#comment"),
        SUITE("suite"),
        TEST("test");

        companion object {
            fun classify(element: Node) =
                values().firstOrNull { it.nodeName == element.nodeName } ?:
                        throw AssertionError("Unknown type: ${element.nodeName}")
        }
    }
}
