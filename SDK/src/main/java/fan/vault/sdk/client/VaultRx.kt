package fan.vault.sdk.client

import android.content.Context
import com.solana.core.PublicKey
import fan.vault.sdk.models.CreatorNFTProfile
import fan.vault.sdk.models.JsonMetadataFileExt
import fan.vault.sdk.models.OneTimePasswordRequest
import io.reactivex.rxjava3.core.Single
import kotlinx.coroutines.rx3.rxSingle

class VaultRx(applicationContext: Context) : VaultBase(applicationContext) {

    /**
     * Request that a new OTP be sent to the given email address.
     *
     * @param emailAddress Email address to send OTP to.
     */
    fun requestGenerateOtp(emailAddress: String) = rxSingle {
        proteusAPIWorker.requestOneTimePassword(
            OneTimePasswordRequest(
                emailAddress,
                walletWorker.loadWallet().publicKey.toBase58()
            )
        )
    }

    /**
     * List claimable NFTs from the Social Wallet associated with the given email address.
     *
     * @param emailAddress Email address for desired Social Wallet.
     * @param includeCreatorData Specify if creator metadata retrieval is required. Default=true.
     * @return List of claimable NFTs from Social Wallet and their associated metadata.
     */
    fun listClaimableNFTsLinkedTo(
        emailAddress: String,
        includeCreatorData: Boolean = true
    ) = rxSingle {
        claimNFTWorker.getClaimableNfts(emailAddress, includeCreatorData)
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
     * Initiate a claim to transfer an NFT from a user's Social Wallet to their App Wallet.
     *
     * @param nftMint Mint address of NFT being claimed.
     * @param emailAddress Email address associated with user's Social Wallet.
     * @param newOtp One time password linking user's email and device app wallet. If omitted, will attempt to use cached OTP from any previous claims.
     * @return Transaction result
     */
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
     * Initiate a file decryption.
     *
     * @param file File to decrypt.
     * @return ByteArray
     */
    fun decryptFile(file: JsonMetadataFileExt): Single<ByteArray> = rxSingle { dmcContentWorker.decryptFile(file) }

}