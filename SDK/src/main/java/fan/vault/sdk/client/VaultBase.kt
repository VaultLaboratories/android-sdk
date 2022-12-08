package fan.vault.sdk.client

import android.content.Context
import fan.vault.sdk.workers.*

abstract class VaultBase(private val applicationContext: Context) {
    private val storageWorker by lazy { StorageWorker(applicationContext) }
    protected val proteusAPIWorker by lazy { ProteusAPIWorker.create() }
    protected val solanaWorker by lazy { SolanaWorker(proteusAPIWorker) }
    protected val walletWorker by lazy { WalletWorker(storageWorker) }
    protected val claimNFTWorker by lazy { ClaimNFTWorker(proteusAPIWorker, solanaWorker) }
    protected val creatorWorker by lazy { CreatorWorker(proteusAPIWorker) }
    protected val litProtocolWorker by lazy {LitProtocolWorker(walletWorker)}
    protected val dmcContentWorker by lazy {DMCContentWorker(litProtocolWorker)}

    /**
     * Get public key of user's App Wallet.
     *
     * @return Stringified public key of user's App Wallet.
     */
    fun getAppWalletPublicKey(): String = walletWorker.loadWallet().publicKey.toString()

    /**
     * Load cached One Time Password from secure storage, if available.
     *
     * @return One Time Password cached in secure storage or null if not available.
     */
    fun getOtp(): String? = storageWorker.loadOtp()

    /**
     * Cache new One Time Password for use with future requests.
     *
     * @param otp One Time Password to cache.
     */
    fun saveOtp(otp: String) = storageWorker.saveOtp(otp)

    /**
     * Clear cached One Time Password.
     */
    fun clearCachedOtp() = storageWorker.clearOtp()

    /**
     * Clear shared preferences.
     */
    fun clearSharedPrefs() = storageWorker.clearAllStoredInfo()

}