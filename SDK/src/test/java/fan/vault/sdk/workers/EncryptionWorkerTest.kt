package fan.vault.sdk.workers

import org.junit.Assert.*
import org.junit.Test
import java.io.File


class EncryptionWorkerTest {

    @Test
    fun `Can output a byte array`() {
        val symmetricKey = "54d61c43b9832ce5652b09404ceddfae93603004aa4684a86bf64bcbdf9e6a16"
        val file =
            File("/Users/anthonyedwards/IdeaProjects/vault-android-sdk/SDK/src/test/java/TestResources/exampleEncryptedData").readBytes()

        val worker = EncryptionWorker()
        val result = worker.decryptWithSymmetricKey(file, symmetricKey)
        assertNotNull(result)
        assertEquals(9159350, result.size)
    }
}