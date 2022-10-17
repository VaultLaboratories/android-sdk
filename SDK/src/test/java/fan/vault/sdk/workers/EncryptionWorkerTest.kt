package fan.vault.sdk.workers

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import okhttp3.OkHttpClient
import okhttp3.Request
import org.junit.Assert.*
import org.junit.Ignore
import org.junit.Test
import java.util.*

class EncryptionWorkerTest {

    private val url = "https://arweave.net/zPUcS81IIrJQ0arZjBBoAyOW_bG1PSKxKXPNEL2eKa0"
    private val client = OkHttpClient()

    @Test
    fun `Can output a byte array`() {
        val symmetricKey = "54d61c43b9832ce5652b09404ceddfae93603004aa4684a86bf64bcbdf9e6a16"
        val request = Request.Builder()
            .url(url)
            .get()
            .build()
        val data = client
            .newCall(request)
            .execute()
            .body?.bytes()

        val worker = EncryptionWorker()
        val result = worker.decryptWithSymmetricKey(data!!, symmetricKey)
        assertNotNull(result)
        assertEquals(9159350, result.size)
    }
}