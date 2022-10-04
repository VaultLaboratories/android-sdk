package fan.vault.sdk

import android.content.Context
import fan.vault.sdk.workers.EncryptionWorker
import fan.vault.sdk.workers.KeyInfoStorageWorker

object Vault {

    private var applicationContext: Context? = null
    private lateinit var keyStore: KeyInfoStorageWorker
    private val encryptionWorker = EncryptionWorker()

    fun initialize(context: Context) {
        applicationContext = context
        keyStore = KeyInfoStorageWorker(context)
    }

    fun initiateClaimNFTsLinkedTo(emailAddress: String) {
        val walletData = keyStore.loadWalletData() ?: encryptionWorker.generateWalletData()
    }
}