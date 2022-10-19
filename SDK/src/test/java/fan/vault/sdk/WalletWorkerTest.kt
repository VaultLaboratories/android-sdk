package fan.vault.sdk

import fan.vault.sdk.workers.WalletWorker
import junit.framework.Assert.assertEquals
import org.junit.Test
import org.mockito.kotlin.mock

class WalletWorkerTest {

    @Test
    fun genWalletData() {
        val walletWorker = WalletWorker(mock())
        val seedWords =
            "transfer frown island economy raccoon champion wisdom talent tragic scrub kangaroo balcony twenty miracle soul bind abuse practice help crane betray enjoy artwork clever"

        val wallet = walletWorker.generateWalletData(seedWords.split(" "))
        assertEquals(wallet.second.publicKey.toBase58(), "FHreS1zRRqDKYfkZzoCKCPyxPNqwFFCky15qWpcvZJTT")
    }
}