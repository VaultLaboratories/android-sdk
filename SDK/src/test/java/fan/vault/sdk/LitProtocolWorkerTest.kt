package fan.vault.sdk

import fan.vault.sdk.workers.EncryptionWorker
import fan.vault.sdk.workers.LitProtocolWorker
import junit.framework.Assert.assertEquals
import org.junit.Test
import org.mockito.Mockito.mock
import org.mockito.kotlin.whenever
import org.p2p.solanaj.core.Account

class LitProtocolWorkerTest {

    private val encryptionWorker = mock(EncryptionWorker::class.java)

    @Test
    fun genAuthSig() {
        val litProtocolWorker = LitProtocolWorker(encryptionWorker)
        val seeds =
            "transfer frown island economy raccoon champion wisdom talent tragic scrub kangaroo balcony twenty miracle soul bind abuse practice help crane betray enjoy artwork clever"
        val wallet = Account
            .fromBip44MnemonicWithChange(seeds.split(" "), "")

        whenever(encryptionWorker.generateWalletData()).thenReturn(wallet)
        val authSig = litProtocolWorker.genAuthSig()
        println(authSig)
        assertEquals("FHreS1zRRqDKYfkZzoCKCPyxPNqwFFCky15qWpcvZJTT", authSig.address)

    }
}