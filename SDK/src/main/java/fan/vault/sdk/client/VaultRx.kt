package fan.vault.sdk.client

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

class VaultRx(applicationContext: Context) : VaultBase(applicationContext) {

    fun requestGenerateOtp(emailAddress: String) =
        rxSingle {
            proteusAPIWorker.requestOneTimePassword(
                OneTimePasswordRequest(
                    emailAddress,
                    walletWorker.loadWallet().publicKey.toBase58()
                )
            )
        }

    suspend fun listClaimableNftsLinkedTo(emailAddress: String) =
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