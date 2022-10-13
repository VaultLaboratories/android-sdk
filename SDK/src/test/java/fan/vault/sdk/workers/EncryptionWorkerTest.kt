package fan.vault.sdk.workers

import org.junit.Assert.*
import org.junit.Ignore
import org.junit.Test
import java.util.*

class EncryptionWorkerTest {

    @Ignore
    @Test
    fun `Can output a byte array`() {
        val base64SymmetricKey = "VNYcQ7mDLOVlKwlATO3frpNgMASqRoSoa/ZLy9+eahY="
        val symmetricKey = Base64.getDecoder().decode(base64SymmetricKey).toString()
        val worker = EncryptionWorker()
        val result = worker.decryptWithSymmetricKey(ByteArray(32), symmetricKey)
        assertNotNull(result)
        assertEquals(32, result.size)
    }
}