package fan.vault.sdk

import fan.vault.sdk.models.*
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

    @Test(timeout = 60000)
    fun getSymmetricKeyTest() {
        val litProtocolWorker = LitProtocolWorker(encryptionWorker)
        val seeds =
            "transfer frown island economy raccoon champion wisdom talent tragic scrub kangaroo balcony twenty miracle soul bind abuse practice help crane betray enjoy artwork clever"
        val wallet = Account
            .fromBip44MnemonicWithChange(seeds.split(" "), "")
        val accessConditions = AccessControlConditions(
            "balanceOfMetaplexCollection",
            listOf("6mDdR4rGjF5MbF3V81VmZf3e7kJKAeemJasBnwqeiNH1"),
            listOf(),
            PdaInterface(0, emptyMap()),
            "",
            "solanaDevnet",
            ReturnValueTest("", ">", "0")
        )
        val encryptedSymmetricKey =
            "074bb96eec7ef793c71219abbfae4813bd1e300b8539e3b6c4415d549e2c260ef781cee0206be8272b8469a29ae00b066adfac7e4ea1fc4f5d3cca53a02c9d7ef6efd839297778c28d8f7a13682ccab6f31709094ee2a1db382649930178228a3bb789ec2976cbadaa16a0bed48e4e9ca1c643ee005d266c824d7e6fe91c7d0a00000000000000200322c179ceff51e70d85792841c54ed0c0b3bf8b1b781346d25429a8de87ab63e5cd1e0e3ee4fd73b9515ad3065a2891"
        whenever(encryptionWorker.generateWalletData()).thenReturn(wallet)
        val encryptionKeyResponse =
            litProtocolWorker.getSymmetricKey(listOf(accessConditions), encryptedSymmetricKey)

        assertEquals(
            "54d61c43b9832ce5652b09404ceddfae93603004aa4684a86bf64bcbdf9e6a16",
            encryptionKeyResponse.symmetricKey
        )
    }
}