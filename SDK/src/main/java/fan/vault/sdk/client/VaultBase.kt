package fan.vault.sdk.client

import android.content.Context
import fan.vault.sdk.workers.*

abstract class VaultBase(private val applicationContext: Context) {
    protected val storageWorker by lazy { StorageWorker(applicationContext) }
    protected val walletWorker by lazy { WalletWorker(storageWorker) }
    protected val litProtocolWorker by lazy { LitProtocolWorker(walletWorker) }
    protected lateinit var claimNFTWorker: ClaimNFTWorker
    protected lateinit var proteusAPIWorker: ProteusAPIWorker

    fun getAppWalletPublicKey(): String = walletWorker.loadWallet().publicKey.toString()

    fun getOtp(): String? = storageWorker.loadOtp()

    fun saveOtp(otp: String) = storageWorker.saveOtp(otp)

}