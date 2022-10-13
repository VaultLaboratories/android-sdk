package fan.vault.sdk.workers

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.google.common.util.concurrent.MoreExecutors
import com.metaplex.lib.Metaplex
import com.metaplex.lib.drivers.indenty.ReadOnlyIdentityDriver
import com.metaplex.lib.drivers.storage.OkHttpSharedStorageDriver
import com.metaplex.lib.modules.nfts.models.NFT
import com.metaplex.lib.solana.SolanaConnectionDriver
import com.solana.networking.RPCEndpoint
import fan.vault.sdk.models.JsonMetadataExt
import fan.vault.sdk.models.NftTypes
import fan.vault.sdk.models.NftWithArweave
import okhttp3.OkHttpClient
import okhttp3.Request
import org.bitcoinj.crypto.MnemonicCode
import org.p2p.solanaj.core.Account
import java.util.concurrent.Executors

class SolanaWorker {
    private val executor = Executors.newFixedThreadPool(10)
    private val cache = mutableMapOf<String, NftWithArweave>()

    suspend fun listNFTs(
        walletAddress: String,
        allowedNftTypes: List<NftTypes> = listOf(NftTypes.ALBUM)
    ): List<NftWithArweave> {
        val wallet = com.solana.core.PublicKey(walletAddress)
        val solanaIdentityDriver = ReadOnlyIdentityDriver(wallet, solanaConnection.solanaRPC)
        val storageDriver = OkHttpSharedStorageDriver()
        val metaplex = Metaplex(solanaConnection, solanaIdentityDriver, storageDriver)

        val candidates = metaplex.nft
            .findAllByOwner(wallet)
            .getOrNull()
            ?.filterNotNull()

        val toLoad = candidates
            ?.filterNot { cache.contains(it.mint.toBase58()) }

        fetchArweaveMetadata(toLoad)
            .forEach { cache[it.nft.mint.toBase58()] = it }

        return candidates
            ?.mapNotNull { cache[it.mint.toBase58()] }
            ?.filter {
                val type =
                    it.arweave?.attributes?.firstOrNull() { it.trait_type.equals("type") }?.value
                allowedNftTypes.contains(NftTypes.fromText(type?.toString()))
            } ?: emptyList()
    }

    fun fetchArweaveMetadata(
        nfts: List<NFT>?
    ): List<NftWithArweave> {
        val service = MoreExecutors.listeningDecorator(executor)
        val client = OkHttpClient()

        return nfts
            ?.map { nft ->
                // we're using non-standard data in the files section so nft.metadata() in not usable
                // at the moment. Once we have fully agreed standard, we can make contribution to the metaplex library
                service.submit<NftWithArweave> {
                    val request = Request.Builder()
                        .url(nft.uri)
                        .get()
                        .build()

                    val arweave: JsonMetadataExt? = client
                        .newCall(request)
                        .execute()
                        .body?.string()
                        ?.let { jacksonObjectMapper().readValue(it) }

                    NftWithArweave(
                        nft = nft,
                        arweave = arweave
                    )
                }
            }
            ?.mapNotNull { it.get() } ?: emptyList()
    }

    companion object {
        val solanaConnection = SolanaConnectionDriver(RPCEndpoint.devnetSolana)
    }
}