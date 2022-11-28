package fan.vault.sdk

import android.content.Context
import android.media.MediaPlayer
import com.solana.core.Account
import com.solana.core.PublicKey
import fan.vault.sdk.models.OneTimePasswordRequest
import fan.vault.sdk.workers.*
import io.reactivex.rxjava3.core.Single
import kotlinx.coroutines.rx3.rxSingle

import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.util.concurrent.Executors
import java.util.concurrent.Future

class VaultRx(val applicationContext: Context) {

    private val storageWorker by lazy { StorageWorker(applicationContext) }
    private val walletWorker by lazy { WalletWorker(storageWorker) }
    private val litProtocolWorker by lazy { LitProtocolWorker(walletWorker) }
    private lateinit var claimNFTWorker: ClaimNFTWorker
    private lateinit var proteusAPIWorker: ProteusAPIWorker

    fun getAppWalletPublicKey(): String = walletWorker.loadWallet().publicKey.toString()

    fun requestGenerateOtp(emailAddress: String) =
        rxSingle {
            proteusAPIWorker.requestOneTimePassword(
                OneTimePasswordRequest(
                    emailAddress,
                    walletWorker.loadWallet().publicKey.toBase58()
                )
            )
        }


    fun getOtp(): String? = storageWorker.loadOtp()

    fun saveOtp(otp: String) = storageWorker.saveOtp(otp)

    fun listClaimableNftsLinkedTo(emailAddress: String) =
        rxSingle { claimNFTWorker.getClaimableNfts(emailAddress) }

    fun initiateClaimNFTLinkedTo(
        nftAddress: PublicKey,
        emailAddress: String,
        appWallet: Account,
        newOtp: String? = null
    ): Single<String> {
        val otp = newOtp ?: getOtp() ?: throw Throwable("OTP cannot be null")
        return rxSingle { claimNFTWorker.claim(nftAddress, emailAddress, appWallet, otp) ?: "" }
    }

}