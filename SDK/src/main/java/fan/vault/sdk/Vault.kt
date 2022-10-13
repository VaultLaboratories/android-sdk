package fan.vault.sdk

import android.content.Context
import fan.vault.sdk.workers.EncryptionWorker
import fan.vault.sdk.workers.StorageWorker

object Vault {

    private var applicationContext: Context? = null
    private lateinit var keyStore: StorageWorker
    private val encryptionWorker = EncryptionWorker()

    fun initialize(context: Context) {
        applicationContext = context
        keyStore = StorageWorker(context)
    }

    fun initiateClaimNFTsLinkedTo(emailAddress: String) {
        val walletData = keyStore.loadWalletData()
    }
}