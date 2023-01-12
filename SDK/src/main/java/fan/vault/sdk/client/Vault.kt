package fan.vault.sdk.client

import android.content.Context
import com.solana.core.PublicKey
import fan.vault.sdk.models.JsonMetadataFileExt
import fan.vault.sdk.models.NftWithMetadata
import fan.vault.sdk.models.OneTimePasswordRequest
import fan.vault.sdk.utils.APIUtils

class Vault(applicationContext: Context) : VaultBase(applicationContext) {


    /**
     * Based on the auth provider a new OTP will be returned
     * (apple/google) or sent via email to the specified address
     *
     * @param provider authentication provider: google.com; apple.com; email
     * @param guid this is either the Email address to send OTP to or the UID from the auth provider
     * @param token this is the ID token retrieve from the Auth provider (google or apple)
     * @param nonce this is the nonce value returned from auth with Apple ID, only needed for Apple
     * sign in
     * authentication provider
     * @throws error if it can't verify the token passed in for the provider or if the provider is
     * not recognised
     */
    suspend fun requestGenerateOtp(
        provider: String,
        guid: String,
        token: String?,
        nonce: String?
    ): String? {
        val otpRequest = proteusAPIWorker.requestOneTimePassword(
            OneTimePasswordRequest(
                guid,
                provider,
                walletWorker.loadWallet().publicKey.toBase58(),
                token,
                nonce
            )
        )
        if (otpRequest.isSuccessful) {
            return otpRequest.body()
        }
        throw APIUtils.classifyErrorIfKnown(otpRequest.errorBody()?.string())
    }

    /**
     * List claimable NFTs from the Social Wallet associated with the given GUID and auth provider.
     *
     * @param provider auth provider for desired Social Wallet.
     * @param guid guid (email or UID from auth provider) for desired Social Wallet.
     * @param includeCreatorData Specify if creator metadata retrieval is required. Default=true.
     * @return List of claimable NFTs from Social Wallet and their associated metadata.
     */
    suspend fun listClaimableNFTsLinkedTo(
        guid: String,
        provider: String,
        includeCreatorData: Boolean = true
    ): List<NftWithMetadata> {
        val socialWalletReq = proteusAPIWorker.getSocialWalletAddress(guid, provider)
        if (socialWalletReq.isSuccessful) {
            socialWalletReq.body()?.wallet?.let {
                return solanaWorker.listNFTsWithMetadata(
                    it,
                    includeCreatorData = includeCreatorData
                )
            }
        }
        throw APIUtils.classifyErrorIfKnown(socialWalletReq.errorBody().toString())
    }

    /**
     * List claimed NFTs from the user's App Wallet on their current device.
     *
     * @param includeCreatorData Specify if creator metadata retrieval is required. Default=true.
     * @return List of claimed NFTs and associated metadata from the user's App Wallet
     */
    suspend fun listClaimedNFTs(
        includeCreatorData: Boolean = true
    ) =
        solanaWorker.listNFTsWithMetadata(
            walletWorker.loadWallet().publicKey.toBase58(),
            includeCreatorData = includeCreatorData
        )

    /**
     * List featured drops
     *
     * @return List of featured drops including associated creator data
     */
    suspend fun listFeaturedDrops() = storeWorker.getFeaturedDrops()

    /**
     * Initiate a claim to transfer an NFT from a user's Social Wallet to their App Wallet.
     *
     * @param nftMint Mint address of NFT being claimed.
     * @param guid Email address or Auth UID associated with user's Social Wallet.
     * @param provider auth provider (google.com, apple.com, email)
     * @param newOtp One time password linking user's email and device app wallet. If omitted, will attempt to use cached OTP from any previous claims.
     * @return Transaction result
     */
    suspend fun initiateClaimNFTLinkedTo(
        nftMint: PublicKey,
        guid: String,
        provider: String,
        newOtp: String? = null
    ): String? {
        val otp = newOtp ?: getOtp() ?: throw Throwable("OTP cannot be null")
        return claimNFTWorker.claim(
            nftMint,
            guid,
            provider,
            walletWorker.loadWallet(),
            otp
        )?.also {
            saveOtp(otp)
        }
    }

    /**
     * Initiate a file decryption.
     *
     * @param file File to decrypt.
     * @return ByteArray
     */
    suspend fun decryptFile(file: JsonMetadataFileExt): ByteArray =
        dmcContentWorker.decryptFile(file)
}