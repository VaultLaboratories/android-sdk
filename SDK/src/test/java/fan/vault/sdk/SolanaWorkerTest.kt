package fan.vault.sdk

import android.util.Base64
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import fan.vault.sdk.workers.SolanaWorker
import io.mockk.every
import io.mockk.mockkStatic
import io.mockk.slot
import junit.framework.Assert.assertEquals
import junit.framework.Assert.assertTrue
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test

class SolanaWorkerTest {
    @Before
    fun `Bypass android_util_Base64 to java_util_Base64`() {
        mockkStatic(Base64::class)
        val arraySlot = slot<ByteArray>()

        every {
            Base64.encodeToString(capture(arraySlot), Base64.DEFAULT)
        } answers {
            java.util.Base64.getEncoder().encodeToString(arraySlot.captured)
        }

        val stringSlot = slot<String>()
        every {
            Base64.decode(capture(stringSlot), Base64.DEFAULT)
        } answers {
            java.util.Base64.getDecoder().decode(stringSlot.captured)
        }
    }

    @Test
    fun shouldReturnListNfts() {
        val worker = instance()

        runBlocking {
            val nfts = worker
                .listNFTsWithMetadata("FHreS1zRRqDKYfkZzoCKCPyxPNqwFFCky15qWpcvZJTT") // We might need to consider using different wallet for test

            assertTrue(nfts.size >= 3)
        }
    }

    private fun instance() = SolanaWorker()
}