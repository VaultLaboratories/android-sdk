package fan.vault.sdk

import android.content.Context
import fan.vault.sdk.workers.EncryptionWorker
import fan.vault.sdk.workers.StorageWorker
import fan.vault.sdk.workers.WalletWorker

object Vault {

    private var applicationContext: Context? = null
    private lateinit var storageWorker: StorageWorker
    private lateinit var walletWorker: WalletWorker
    private val encryptionWorker = EncryptionWorker()

    fun initialize(context: Context) {
        applicationContext = context
        storageWorker = StorageWorker(context)
        walletWorker = WalletWorker(storageWorker)
    }

    fun initiateClaimNFTsLinkedTo(emailAddress: String) {}

    fun getAppWalletPublicKey(): String = walletWorker.loadWallet().publicKey.toString()

}