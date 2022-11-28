package fan.vault.sdk

import android.content.Context
import android.media.MediaPlayer
import com.solana.core.PublicKey
import fan.vault.sdk.models.OneTimePasswordRequest
import fan.vault.sdk.workers.*

import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.util.concurrent.Executors
import java.util.concurrent.Future

class Vault(val applicationContext: Context) {

    private lateinit var storageWorker: StorageWorker
    private lateinit var walletWorker: WalletWorker
    private lateinit var claimNFTWorker: ClaimNFTWorker
    private lateinit var proteusAPIWorker: ProteusAPIWorker
    private val litProtocolWorker by lazy { LitProtocolWorker(walletWorker) }

    fun initialize() {
        storageWorker = StorageWorker(applicationContext)
        walletWorker = WalletWorker(storageWorker)
    }

    fun getAppWalletPublicKey(): String = walletWorker.loadWallet().publicKey.toString()

    suspend fun requestGenerateOtp(emailAddress: String) =
        proteusAPIWorker.requestOneTimePassword(
            OneTimePasswordRequest(
                emailAddress,
                walletWorker.loadWallet().publicKey.toBase58()
            )
        )

    fun getOtp(): String? = storageWorker.loadOtp()

    fun saveOtp(otp: String) = storageWorker.saveOtp(otp)

    suspend fun listClaimableNftsLinkedTo(emailAddress: String) =
        claimNFTWorker.getClaimableNfts(emailAddress)

    suspend fun initiateClaimNFTLinkedTo(
        nftAddress: PublicKey,
        emailAddress: String,
        newOtp: String? = null
    ): String? {
        val otp = newOtp ?: getOtp() ?: throw Throwable("OTP cannot be null")
        return claimNFTWorker.claim(nftAddress, emailAddress, otp)
    }

    suspend fun initiateClaimAllNFTsLinkedTo(
        emailAddress: String,
        newOtp: String? = null
    ): List<String?> {
        val otp = newOtp ?: getOtp() ?: throw Throwable("OTP cannot be null")
        return listClaimableNftsLinkedTo(emailAddress)
            .filterNotNull()
            .map {
                claimNFTWorker.claim(it.nft.mint, emailAddress, otp)
            }
    }


}