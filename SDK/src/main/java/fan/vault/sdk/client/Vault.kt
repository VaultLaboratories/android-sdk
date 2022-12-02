package fan.vault.sdk.client

import android.content.Context
import com.solana.core.PublicKey
import fan.vault.sdk.models.OneTimePasswordRequest

class Vault(applicationContext: Context) : VaultBase(applicationContext) {

    /**
     * Request that a new OTP be sent to the given email address.
     *
     * @param emailAddress Email address to send OTP to.
     */
    suspend fun requestGenerateOtp(emailAddress: String) =
        proteusAPIWorker.requestOneTimePassword(
            OneTimePasswordRequest(
                emailAddress,
                walletWorker.loadWallet().publicKey.toBase58()
            )
        )

    /**
     * List claimable NFTs from the Social Wallet associated with the given email address.
     *
     * @param emailAddress Email address for desired Social Wallet.
     * @return List of claimable NFTs from Social Wallet and their associated metadata.
     */
    suspend fun listClaimableNFTsLinkedTo(emailAddress: String) =
        claimNFTWorker.getClaimableNfts(emailAddress)

    /**
     * Initiate a claim to transfer an NFT from a user's Social Wallet to their App Wallet.
     *
     * @param nftMint Mint address of NFT being claimed.
     * @param emailAddress Email address associated with user's Social Wallet.
     * @param newOtp One time password linking user's email and device app wallet. If omitted, will attempt to use cached OTP from any previous claims.
     * @return Transaction result
     */
    suspend fun initiateClaimNFTLinkedTo(
        nftMint: PublicKey,
        emailAddress: String,
        newOtp: String? = null
    ): String? {
        val otp = newOtp ?: getOtp() ?: throw Throwable("OTP cannot be null")
        newOtp?.let { saveOtp(it) }
        return claimNFTWorker.claim(
            nftMint,
            emailAddress,
            walletWorker.loadWallet(),
            otp
        )
    }
}