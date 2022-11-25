package fan.vault.sdk.workers

import com.solana.core.Account
import com.solana.core.PublicKey
import fan.vault.sdk.models.NftWithMetadata

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
            solanaWorker.signAndSendTransaction(it.hashTrx, appWallet)
        }
    }

    suspend fun getClaimableNfts(userEmailAddress: String): List<NftWithMetadata> =
        proteusAPIWorker.getSocialWalletAddress(userEmailAddress).let {
            solanaWorker.listNFTsWithMetadata(it.wallet)
        }
}