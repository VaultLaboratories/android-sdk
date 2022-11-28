package fan.vault.sdk.client

import android.content.Context
import fan.vault.sdk.workers.*

abstract class VaultBase(private val applicationContext: Context) {
    val storageWorker by lazy { StorageWorker(applicationContext) }
    val walletWorker by lazy { WalletWorker(storageWorker) }
    val litProtocolWorker by lazy { LitProtocolWorker(walletWorker) }
    lateinit var claimNFTWorker: ClaimNFTWorker
    lateinit var proteusAPIWorker: ProteusAPIWorker

    fun getAppWalletPublicKey(): String = walletWorker.loadWallet().publicKey.toString()

    open fun getOtp(): String? = storageWorker.loadOtp()

    open fun saveOtp(otp: String) = storageWorker.saveOtp(otp)

}