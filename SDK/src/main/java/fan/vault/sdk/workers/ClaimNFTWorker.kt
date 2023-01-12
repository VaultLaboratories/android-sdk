package fan.vault.sdk.workers

import com.solana.core.Account
import com.solana.core.PublicKey
import fan.vault.sdk.models.AuthProviders
import fan.vault.sdk.models.NftWithMetadata
import fan.vault.sdk.utils.APIUtils
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*

class ClaimNFTWorker(val proteusAPIWorker: ProteusAPIWorker, val solanaWorker: SolanaWorker) {

    suspend fun claim(
        nft: PublicKey,
        guid: String,
        provider: String,
        appWallet: Account,
        withOtp: String
    ): String? {
        val txResponse = proteusAPIWorker.getSocialToAppWalletClaimTransaction(
            guid = guid,
            provider = provider,
            appWallet = appWallet.publicKey.toBase58(),
            mint = nft.toBase58(),
            otp = withOtp
        )
        if (txResponse.isSuccessful) {
            return flow {
                emit(solanaWorker.signAndSendTransaction(txResponse.body()!!.hashTrx, appWallet))
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
        } else {
            throw APIUtils.classifyErrorIfKnown(txResponse.errorBody()?.string())
        }
    }

}