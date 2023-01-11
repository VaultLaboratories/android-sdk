package fan.vault.sdk.client

import android.content.Context
import com.solana.core.PublicKey
import fan.vault.sdk.models.*
import fan.vault.sdk.utils.APIUtils
import io.reactivex.rxjava3.core.Single
import kotlinx.coroutines.rx3.rxSingle

class VaultRx(applicationContext: Context) : VaultBase(applicationContext) {

    /**
     * Request that a new OTP be sent to the given email address.
     *
     * @param emailAddress Email address to send OTP to.
     */
    @Deprecated("Old social wallet format")
    fun requestGenerateOtp(emailAddress: String) = rxSingle {
        proteusAPIWorker.requestOneTimePassword(
            OneTimePasswordRequest(
                emailAddress,
                walletWorker.loadWallet().publicKey.toBase58()
            )
        )
    }

    /**
     * Based on the auth provider a new OTP will be returned
     * (apple/google) or sent via email to the specified address
     *
     * @param provider authentication provider: google.com; apple.com; email
     * @param guid this is either the Email address to send OTP to or the UID from the
     * authentication provider
     * @param token this is the ID token retrieve from the Auth provider (google or apple)
     * @param nonce this is the nonce value returned from auth with Apple ID, only needed for Apple
     * @throws error if it can't verify the token passed in for the provider or if the provider is
     * not recognised
     */
    suspend fun requestGenerateOtpV2(
        provider: String,
        guid: String,
        token: String?,
        nonce: String?
    ): Single<String> {
        return rxSingle {
            val otpRequest = proteusAPIWorker.requestOneTimePasswordV2(
                OneTimePasswordRequestV2(
                    guid,
                    provider,
                    walletWorker.loadWallet().publicKey.toBase58(),
                    token,
                    nonce
                )
            )
            if (otpRequest.isSuccessful) {
                otpRequest.body()
            }
            throw APIUtils.classifyErrorIfKnown(otpRequest.errorBody()?.string())
        }
    }

    /**
     * List claimable NFTs from the Social Wallet associated with the given email address.
     *
     * @param emailAddress Email address for desired Social Wallet.
     * @param includeCreatorData Specify if creator metadata retrieval is required. Default=true.
     * @return List of claimable NFTs from Social Wallet and their associated metadata.
     */
    @Deprecated("Old social wallet format")
    fun listClaimableNFTsLinkedTo(
        emailAddress: String,
        includeCreatorData: Boolean = true
    ) = rxSingle {
        claimNFTWorker.getClaimableNfts(emailAddress, includeCreatorData)
    }

    /**
     * List claimable NFTs from the Social Wallet associated with the given email address.
     *
     * @param provider auth provider for desired Social Wallet.
     * @param guid guid (email or UID from auth provider) for desired Social Wallet.
     * @param includeCreatorData Specify if creator metadata retrieval is required. Default=true.
     * @return List of claimable NFTs from Social Wallet and their associated metadata.
     */
    suspend fun listClaimableNFTsLinkedToV2(
        provider: AuthProviders,
        guid: String,
        includeCreatorData: Boolean = true
    ) = rxSingle {
        claimNFTWorker.getClaimableNftsV2(provider, guid, includeCreatorData)
    }

    /**
     * List claimed NFTs from the user's App Wallet on their current device.
     *
     * @param includeCreatorData Specify if creator metadata retrieval is required. Default=true.
     * @return List of claimed NFTs and associated metadata from the user's App Wallet
     */
    fun listClaimedNFTs(
        includeCreatorData: Boolean = true
    ) = rxSingle {
        solanaWorker.listNFTsWithMetadata(
            walletWorker.loadWallet().publicKey.toBase58(),
            includeCreatorData = includeCreatorData
        )
    }

    /**
     * List featured drops
     *
     * @return List of featured drops including associated creator data
     */
    fun listFeaturedDrops() = rxSingle {
        storeWorker.getFeaturedDrops()
    }

    /**
     * Initiate a claim to transfer an NFT from a user's Social Wallet to their App Wallet.
     *
     * @param nftMint Mint address of NFT being claimed.
     * @param emailAddress Email address associated with user's Social Wallet.
     * @param newOtp One time password linking user's email and device app wallet. If omitted, will attempt to use cached OTP from any previous claims.
     * @return Transaction result
     */
    @Deprecated("Old social wallet format")
    fun initiateClaimNFTLinkedTo(
        nftMint: PublicKey,
        emailAddress: String,
        newOtp: String? = null
    ): Single<String> {
        val otp = newOtp ?: getOtp() ?: throw Throwable("OTP cannot be null")
        return rxSingle {
            claimNFTWorker.claim(
                nftMint,
                emailAddress,
                walletWorker.loadWallet(),
                otp
            )?.also {
                saveOtp(otp)
            } ?: ""
        }
    }

    /**
     * Initiate a claim to transfer an NFT from a user's Social Wallet to their App Wallet.
     *
     * @param nftMint Mint address of NFT being claimed.
     * @param guid Email address or Auth UID associated with user's Social Wallet.
     * @param provider auth provider (google.com, apple.com, email)
     * @param newOtp One time password linking user's email and device app wallet. If omitted, will attempt to use cached OTP from any previous claims.
     * @return Transaction result
     */
    suspend fun initiateClaimNFTLinkedToV2(
        nftMint: PublicKey,
        guid: String,
        provider: AuthProviders,
        newOtp: String? = null
    ): Single<String> {
        val otp = newOtp ?: getOtp() ?: throw Throwable("OTP cannot be null")
        return rxSingle {
            claimNFTWorker.claimV2(
                nftMint,
                guid,
                provider,
                walletWorker.loadWallet(),
                otp
            )?.also {
                saveOtp(otp)
            } ?: ""
        }
    }

    /**
     * Initiate a file decryption.
     *
     * @param file File to decrypt.
     * @return ByteArray
     */
    fun decryptFile(file: JsonMetadataFileExt): Single<ByteArray> =
        rxSingle { dmcContentWorker.decryptFile(file) }

}