package fan.vault.sdk.workers

import com.solana.core.Account
import com.solana.core.PublicKey
import fan.vault.sdk.models.TransactionResponse
import io.reactivex.rxjava3.core.Single

class ClaimNFTWorker(val proteusAPIWorker: ProteusAPIWorker, val solanaWorker: SolanaWorker) {
    fun claim(
        nft: PublicKey,
        userEmailAddress: String,
        appWallet: Account,
        withOtp: String
    ): Single<String> {
        val transaction = proteusAPIWorker.getSocialToAppWalletClaimTransaction(
            userEmailAddress = userEmailAddress,
            appWallet = appWallet.publicKey.toBase58(),
            mint = nft.toBase58(),
            otp = withOtp
        )

        return transaction.flatMap { transaction: TransactionResponse ->
            Single.create<String> { subscriber ->
                solanaWorker.signAndSendTransaction(transaction.hashTrx, appWallet) {
                    it
                        .onSuccess {
                            subscriber.onSuccess(it)
                        }
                        .onFailure {
                            subscriber.onError(it)
                        }
                }
            }
        }
            .map { it }
    }
}