package fan.vault.sdk.workers

import android.util.Log
import com.solana.core.Account
import com.solana.core.PublicKey
import fan.vault.sdk.models.NftWithMetadata
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*

class ClaimNFTWorker(val proteusAPIWorker: ProteusAPIWorker, val solanaWorker: SolanaWorker) {
    suspend fun claim(
        nft: PublicKey,
        userEmailAddress: String,
        appWallet: Account,
        withOtp: String
    ): String? {
        return proteusAPIWorker.getSocialToAppWalletClaimTransaction(
            userEmailAddress = userEmailAddress,
            appWallet = appWallet.publicKey.toBase58(),
            mint = nft.toBase58(),
            otp = withOtp
        ).let {
            flow {
                emit(solanaWorker.signAndSendTransaction(it.hashTrx, appWallet))
            }
                .retry(20) { e ->
                    when (e is Exception) {
                        true -> {
                            delay(2000)
                            true
                        }
                        else -> false
                    }
                }
                .first()
        }
    }

    suspend fun getClaimableNfts(userEmailAddress: String): List<NftWithMetadata?> =
        proteusAPIWorker.getSocialWalletAddress(userEmailAddress).let {
            solanaWorker.listNFTsWithMetadata(it.wallet)
        }
}