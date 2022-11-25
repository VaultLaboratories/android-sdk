package fan.vault.sdk.workers

import android.util.Base64
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.metaplex.lib.Metaplex
import com.metaplex.lib.drivers.indenty.ReadOnlyIdentityDriver
import com.metaplex.lib.drivers.storage.OkHttpSharedStorageDriver
import com.metaplex.lib.modules.nfts.models.NFT
import com.metaplex.lib.solana.SolanaConnectionDriver
import com.solana.Solana
import com.solana.api.sendRawTransaction
import com.solana.core.Account
import com.solana.core.Transaction
import com.solana.networking.OkHttpNetworkingRouter
import com.solana.networking.RPCEndpoint
import fan.vault.sdk.models.JsonMetadataExt
import fan.vault.sdk.models.NftTypes
import fan.vault.sdk.models.NftWithMetadata
import okhttp3.HttpUrl.Companion.toHttpUrlOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import java.util.concurrent.Executors
import java.util.concurrent.Future
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

class SolanaWorker {
    private val executor = Executors.newFixedThreadPool(10)
    private val client = OkHttpClient()

    suspend fun listNFTs(walletAddress: String): List<NFT> {
        val wallet = com.solana.core.PublicKey(walletAddress)
        val solanaIdentityDriver = ReadOnlyIdentityDriver(wallet, solana.api)
        val storageDriver = OkHttpSharedStorageDriver()
        val metaplex = Metaplex(solanaConnection, solanaIdentityDriver, storageDriver)

        return metaplex.nft
            .findAllByOwner(wallet)
            .getOrNull()
            ?.filterNotNull() ?: emptyList()
    }

    suspend fun listNFTsWithMetadata(
        walletAddress: String,
        allowedNftTypes: List<NftTypes> = listOf(NftTypes.ALBUM, NftTypes.SINGLE)
    ): List<NftWithMetadata> {
        val candidates = listNFTs(walletAddress)
            .map { fetchArweaveMetadata(it) }

        return candidates
            .mapNotNull { it.get() }
            .filter {
                val type =
                    it.metadata?.attributes?.firstOrNull() { it.trait_type.equals("type") }?.value
                allowedNftTypes.contains(NftTypes.fromText(type?.toString()))
            }
    }

    fun fetchArweaveMetadata(
        nft: NFT
    ): Future<NftWithMetadata?> {
        // we're using non-standard data in the files section so nft.metadata() in not usable
        // at the moment. Once we have fully agreed standard, we can make contribution to the metaplex library
        return executor.submit<NftWithMetadata> {
            nft.uri.toHttpUrlOrNull()?.let {
                Request.Builder()
                    .url(it)
                    .get()
                    .build()
            }?.let { request ->
                kotlin.runCatching {
                    val arweave: JsonMetadataExt? =
                        client
                            .newCall(request)
                            .execute()
                            .body?.string()
                            ?.let { jacksonObjectMapper().readValue(it) }

                    NftWithMetadata(
                        nft = nft,
                        metadata = arweave
                    )
                }
                    .onFailure { println("Failed to fetch arweave metadata for ${nft.mint.toBase58()}, it will be skipped!") }
                    .getOrNull()
            }
        }
    }

    suspend fun signAndSendTransaction(
        base64EncodedTransaction: String,
        signer: Account
    ): String? {
        val decoded = Base64.decode(base64EncodedTransaction, Base64.DEFAULT)
        val transaction = Transaction.from(decoded)

        transaction.partialSign(signer)

        val serialized = transaction.serialize()

        return suspendCoroutine { continuation ->
            solana.api.sendRawTransaction(serialized) { result ->
                result.onSuccess {
                    continuation.resumeWith(result)
                }.onFailure {
                    continuation.resumeWithException(
                        result.exceptionOrNull() ?: Throwable("Generic error message")
                    )
                }
            }
        }
    }

    companion object {
        val solana = Solana(OkHttpNetworkingRouter(RPCEndpoint.devnetSolana))
        val solanaConnection = SolanaConnectionDriver(RPCEndpoint.devnetSolana)
    }
}