package fan.vault.sdk

import com.squareup.moshi.Json
import fan.vault.sdk.models.WalletData
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
        val secKey = "c79e9539decb594857b978d85ea99b5b17bf345365a783b1476be780e76be8e9d4550b4ad199d5ab06fd8ab89dfd43e5f4623ee82b0f87c20ee6d232cb8791d2"
        val wallet = Account(secKey.toByteArray())
        whenever(encryptionWorker.generateWalletData()).thenReturn(wallet)
        val authSig = litProtocolWorker.genAuthSig()
        println(authSig)

    }
}