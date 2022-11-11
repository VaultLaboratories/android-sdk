package fan.vault.sdk.workers

import android.util.Base64

import fan.vault.sdk.models.TransactionResponse
import io.mockk.every
import io.mockk.mockkStatic
import io.mockk.slot
import junit.framework.Assert.assertEquals
import junit.framework.Assert.assertTrue
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

class SolanaWorkerTest {
    private val storageWorker = Mockito.mock(StorageWorker::class.java)

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

    @Test
    fun shouldSignAndSendTransaction() {
        val worker = instance()
        val hashTrx = "AowfOvMIGMKpHdSMkJngzXiF+R1nhZpXl4ead8v9j2KMRNGWxw4ORAEMBxVDHauoPybbYzxE8DxwVkGNSlpHJA8AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAgAGDE1NcOheAvyv+bxRcoAAg+MRGtu2bkAm/5wdXQJ8rken1FULStGZ1asG/Yq4nf1D5fRiPugrD4fCDubSMsuHkdIayMAB688AuwbzoF9XnkQFo2FzCNJXzqL54DE6mJ3jcVhQyOsVuOXyU8XVGmOO2ep3vzbZGM4vgpLvjEcAM00J+V3TgVRf23jeOe/lnzkzO6KxIRRsdiBzWWuNQTu8yH/6ZEwlvNmeorQ162ZCz63Oge4CfDiNpiav1AqEyomoh3Fva1OSac1YjEGTGcfA31cfJcQU3+q9HrQZS+lxomSRdvMm5PB133e9DwijLEt6TSX1hyKy/MHeJpT5sgfx0Q6rTKN/L2kwSCbxyvNbI19PNeLYInltDmx6X1AtAMntzsjbHnHIO/C4BzQArccr6sxzJRGosQj9TDQA5uKlJYJ+C3BlsePRfEU4nVJ/awTDzVi4bHMaoP21SbbRvAP4KUYG3fbh12Whk9nL4UbO63msHLSF7V9bN5E6jPWFfv8AqS9th6tOQZlmjaIuFlZG82nyqa3jkyOF4l/umJZJMUYrAQcMAwQFAgABBggJCwsKQgxhNeMOztloLAAAAHE5N3A1TzE3VXRqWUVwRWI4UVd0Y0tXTi9hK0Q4MjI0OFhwOEdDNEFSczg9BgAAADEyMzQ1Ng=="

        val seedWords =
            "transfer frown island economy raccoon champion wisdom talent tragic scrub kangaroo balcony twenty miracle soul bind abuse practice help crane betray enjoy artwork clever"

        val latch = CountDownLatch(1)

        val wallet = WalletWorker(storageWorker).generateWalletData(seedWords.split(" "))
        worker.signAndSendTransaction(hashTrx, wallet.second) {
            // We expect this one to fail as it has old blockhash
            assertTrue(it.isFailure)
            assertEquals(it.exceptionOrNull()?.message, "Transaction simulation failed: Blockhash not found")

            latch.countDown()
        }

        assertTrue(latch.await(5L, TimeUnit.SECONDS))
    }

    private fun instance() = SolanaWorker()
}