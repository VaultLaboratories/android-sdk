package fan.vault.sdk

import fan.vault.sdk.workers.EncryptionWorker
import junit.framework.Assert.assertEquals
import org.junit.Test

class EncryptionWorkerTest {

    @Test
    fun genWalletData() {
        val encryptionWorker = EncryptionWorker()
        val seedWords =
            "transfer frown island economy raccoon champion wisdom talent tragic scrub kangaroo balcony twenty miracle soul bind abuse practice help crane betray enjoy artwork clever"

        val wallet = encryptionWorker.generateWalletData(seedWords.split(" "))
        assertEquals(wallet.publicKey.toBase58(), "FHreS1zRRqDKYfkZzoCKCPyxPNqwFFCky15qWpcvZJTT")
    }
}