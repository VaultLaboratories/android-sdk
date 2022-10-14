package fan.vault.sdk.workers

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.google.common.util.concurrent.ListenableFuture
import com.google.common.util.concurrent.MoreExecutors
import com.metaplex.lib.Metaplex
import com.metaplex.lib.drivers.indenty.ReadOnlyIdentityDriver
import com.metaplex.lib.drivers.storage.OkHttpSharedStorageDriver
import com.metaplex.lib.modules.nfts.models.NFT
import com.metaplex.lib.solana.SolanaConnectionDriver
import com.solana.networking.RPCEndpoint
import fan.vault.sdk.models.JsonMetadataExt
import fan.vault.sdk.models.NftTypes
import fan.vault.sdk.models.NftWithMetadata
import okhttp3.OkHttpClient
import okhttp3.Request
import java.util.concurrent.Executors

class SolanaWorker {
    private val executor = Executors.newFixedThreadPool(10)
    private val service = MoreExecutors.listeningDecorator(executor)
    private val client = OkHttpClient()

    suspend fun listNFTs(walletAddress: String): List<NFT> {
        val wallet = com.solana.core.PublicKey(walletAddress)
        val solanaIdentityDriver = ReadOnlyIdentityDriver(wallet, solanaConnection.solanaRPC)
        val storageDriver = OkHttpSharedStorageDriver()
        val metaplex = Metaplex(solanaConnection, solanaIdentityDriver, storageDriver)

        return metaplex.nft
            .findAllByOwner(wallet)
            .getOrNull()
            ?.filterNotNull() ?: emptyList()
    }

    suspend fun listNFTsWithMetadata(
        walletAddress: String,
        allowedNftTypes: List<NftTypes> = listOf(NftTypes.ALBUM)
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
    ): ListenableFuture<NftWithMetadata?> {
        // we're using non-standard data in the files section so nft.metadata() in not usable
        // at the moment. Once we have fully agreed standard, we can make contribution to the metaplex library
        return service.submit<NftWithMetadata> {
            val request = Request.Builder()
                .url(nft.uri)
                .get()
                .build()

            kotlin.runCatching {
                val arweave: JsonMetadataExt? = client
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

    companion object {
        val solanaConnection = SolanaConnectionDriver(RPCEndpoint.devnetSolana)
    }
}