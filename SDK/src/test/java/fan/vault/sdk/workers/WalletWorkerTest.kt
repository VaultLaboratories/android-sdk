package fan.vault.sdk.workers

import junit.framework.Assert.assertEquals
import org.junit.Test
import org.mockito.Mockito

class WalletWorkerTest {
    private val storageWorker = Mockito.mock(StorageWorker::class.java)

    @Test
    fun genWalletData() {
        val walletWorker = WalletWorker(storageWorker)
        val seedWords =
            "transfer frown island economy raccoon champion wisdom talent tragic scrub kangaroo balcony twenty miracle soul bind abuse practice help crane betray enjoy artwork clever"

        val wallet = walletWorker.generateWalletData(seedWords.split(" "))
        assertEquals(wallet.second.publicKey.toBase58(), "FHreS1zRRqDKYfkZzoCKCPyxPNqwFFCky15qWpcvZJTT")
    }
}