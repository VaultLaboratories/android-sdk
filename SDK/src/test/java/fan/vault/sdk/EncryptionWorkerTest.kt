package fan.vault.sdk

import fan.vault.sdk.workers.EncryptionWorker
import org.junit.Test

class EncryptionWorkerTest {

    @Test
    fun genWalletData(){
        val encryptionWorker = EncryptionWorker()
        val wallet = encryptionWorker.generateWalletData()

        println(wallet)

    }
}