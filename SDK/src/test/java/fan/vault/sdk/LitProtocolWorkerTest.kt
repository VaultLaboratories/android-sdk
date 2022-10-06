package fan.vault.sdk

import fan.vault.sdk.models.WalletData
import fan.vault.sdk.workers.EncryptionWorker
import fan.vault.sdk.workers.LitProtocolWorker
import junit.framework.Assert.assertEquals
import org.junit.Test

import org.mockito.Mockito.mock
import org.mockito.kotlin.whenever

class LitProtocolWorkerTest {

    private val encryptionWorker = mock(EncryptionWorker::class.java)

    @Test
    fun genAuthSig() {
        val litProtocolWorker = LitProtocolWorker(encryptionWorker)
        val wallet = WalletData(
            "6X9yJGN4ckEfC7WoMDmbZhZC3ND3GJZGGrwKqmUtQaP",
            "4NnfcSrWzUXccr6qd48mJpbw37b2zk2SXVMkvDq2jLjqPoELAnShKMesnPfsEfJyLBjfWAUVFu5kd3qpzBiVfuFh"
        )
        whenever(encryptionWorker.generateWalletData()).thenReturn(wallet)
        val authSig = litProtocolWorker.genAuthSig()
        println(authSig)

        assertEquals(authSig.address, wallet.publicKey)

    }
}