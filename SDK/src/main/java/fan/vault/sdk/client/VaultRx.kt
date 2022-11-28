package fan.vault.sdk.client

import android.content.Context
import com.solana.core.Account
import com.solana.core.PublicKey
import fan.vault.sdk.models.OneTimePasswordRequest
import io.reactivex.rxjava3.core.Single
import kotlinx.coroutines.rx3.rxSingle

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