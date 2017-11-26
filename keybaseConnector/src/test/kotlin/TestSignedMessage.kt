import org.testng.annotations.Test
import uk.co.jrtapsell.gpgWrapper.GpgWrapper
import java.io.File

class TestSignedMessage {

    @Test
    fun `Checks messages signed by various users are correct`() {
        val james = Keybase.getByUsername("jrtapsell")!!
        val message = File("../gpgTestFiles/trusted/message.txt").readText()
        val signature = File("../gpgTestFiles/trusted/message.txt.asc").readText()
        GpgWrapper.validate(message, signature, *james.keys.toTypedArray())
    }
}