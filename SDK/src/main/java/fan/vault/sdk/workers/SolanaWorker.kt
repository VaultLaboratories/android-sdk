package fan.vault.sdk.workers

import android.util.Base64
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue

import com.metaplex.lib.Metaplex
import com.metaplex.lib.drivers.indenty.ReadOnlyIdentityDriver
import com.metaplex.lib.drivers.storage.OkHttpSharedStorageDriver
import com.metaplex.lib.modules.nfts.models.NFT
import com.metaplex.lib.solana.SolanaConnectionDriver
import com.solana.api.Api
import com.solana.rxsolana.api.sendTransaction
import com.solana.core.Account
import com.solana.core.SerializeConfig
import com.solana.core.Transaction
import com.solana.networking.OkHttpNetworkingRouter
import com.solana.networking.RPCEndpoint
import fan.vault.sdk.models.JsonMetadataExt
import fan.vault.sdk.models.NftTypes
import fan.vault.sdk.models.NftWithMetadata
import fan.vault.sdk.models.TransactionResponse
import fan.vault.sdk.solana.sendRawTransaction
import io.reactivex.Single
import okhttp3.OkHttpClient
import okhttp3.Request
import java.util.concurrent.Executors
import java.util.concurrent.Future

class SolanaWorker {
    private val executor = Executors.newFixedThreadPool(10)
    private val client = OkHttpClient()

    suspend fun listNFTs(walletAddress: String): List<NFT> {
        val wallet = com.solana.core.PublicKey(walletAddress)
        val solanaIdentityDriver = ReadOnlyIdentityDriver(wallet, rpc)
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
    ): Future<NftWithMetadata?> {
        // we're using non-standard data in the files section so nft.metadata() in not usable
        // at the moment. Once we have fully agreed standard, we can make contribution to the metaplex library
        return executor.submit<NftWithMetadata> {
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

    fun signAndSendTransaction(transactionResponse: TransactionResponse, signer: Account, onComplete: (Result<String>) -> Unit) {
        val decoded = Base64.decode(transactionResponse.hashTrx, Base64.DEFAULT)
        val transaction = Transaction.from(decoded)

        val serialized = transaction.serialize(SerializeConfig(false, false))
        val base64Trx: String = Base64.encodeToString(serialized, Base64.DEFAULT)
        transaction.partialSign(signer)

//        val serialized = transaction.serialize()
//        val base64Trx: String = Base64.encodeToString(serialized, Base64.DEFAULT)

        return rpc.sendRawTransaction(base64Trx, onComplete)
    }

    companion object {
        val solanaConnection = SolanaConnectionDriver(RPCEndpoint.devnetSolana)
        val rpc = Api(OkHttpNetworkingRouter(RPCEndpoint.devnetSolana))
    }
}