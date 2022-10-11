package fan.vault.sdk

import fan.vault.sdk.workers.ProteusWorker
import fan.vault.sdk.workers.SolanaWorker
import junit.framework.Assert.assertEquals
import kotlinx.coroutines.runBlocking
import org.junit.Test

class SolanaWorkerTest {

    @Test
    fun shouldReturnListNfts() {
        val worker = instance()

        runBlocking {
            val nfts = worker.listNFTs("FHreS1zRRqDKYfkZzoCKCPyxPNqwFFCky15qWpcvZJTT")
            assertEquals("", nfts)
        }
    }

    private fun instance() = SolanaWorker(ProteusWorker())
}