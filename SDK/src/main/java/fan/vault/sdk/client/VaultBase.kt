package fan.vault.sdk.client

import android.content.Context
import com.solana.Solana
import fan.vault.sdk.workers.*

abstract class VaultBase(private val applicationContext: Context) {
    protected val storageWorker by lazy { StorageWorker(applicationContext) }
    protected val walletWorker by lazy { WalletWorker(storageWorker) }
    protected val proteusAPIWorker by lazy { ProteusAPIWorker.create() }
    protected val solanaWorker by lazy { SolanaWorker() }
    protected val claimNFTWorker by lazy { ClaimNFTWorker(proteusAPIWorker, solanaWorker) }

    fun getAppWalletPublicKey(): String = walletWorker.loadWallet().publicKey.toString()

    fun getOtp(): String? = storageWorker.loadOtp()

    fun saveOtp(otp: String) = storageWorker.saveOtp(otp)

}