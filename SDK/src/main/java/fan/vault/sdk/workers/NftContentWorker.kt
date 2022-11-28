package fan.vault.sdk.workers

import com.solana.core.PublicKey
import fan.vault.sdk.models.DecryptedContent
import fan.vault.sdk.models.EncryptionKeyRequest
import fan.vault.sdk.models.JsonMetadataFileExt
import kotlinx.coroutines.scheduling.DefaultIoScheduler.executor
import okhttp3.OkHttpClient
import okhttp3.Request
import java.util.concurrent.Executors
import java.util.concurrent.Future

class NftContentWorker(
    val litProtocolWorker: LitProtocolWorker,
    val solanaWorker: SolanaWorker,
    val walletWorker: WalletWorker,
    val proteusAPIWorker: ProteusAPIWorker
) {

    private val executor = Executors.newFixedThreadPool(10)

    suspend fun getFileListForNft(nftAddress: PublicKey): List<JsonMetadataFileExt>? {
        val wallet = walletWorker.loadWallet().publicKey.toBase58()
        return solanaWorker.getNftWithMetadata(nftAddress, wallet)?.metadata?.properties?.files
    }

    suspend fun getDecryptedContentForNft(nftAddress: PublicKey): List<DecryptedContent> {
        val wallet = walletWorker.loadWallet().publicKey.toBase58()
        val nftWithMetadata = solanaWorker.getNftWithMetadata(nftAddress, wallet)
        val decryptedFile = nftWithMetadata?.nft?.uri?.let {
            fetchEncryptedData(it).get()
        }?.let {
            // only need the first file for access conditions + encrypted symm key
            val firstFile = nftWithMetadata.metadata?.properties?.files?.get(0) ?: throw Throwable("corrupt data")
            val symmetricKey = proteusAPIWorker.getEncryptionKey(EncryptionKeyRequest(litProtocolWorker.genAuthSig(), firstFile.accessControlConditions!!, firstFile.encryptedSymmetricKey!!))
            litProtocolWorker.decryptWithSymmetricKey(it, symmetricKey.symmetricKey)
        }

    }

    // TODO check this is the most effective way of doing this
    fun fetchEncryptedData(url: String): Future<ByteArray?> {
        val client = OkHttpClient()
        val request = Request.Builder()
            .url(url)
            .get()
            .build()
        return executor.submit<ByteArray?> {
            client
                .newCall(request)
                .execute()
                .body?.bytes()
        }
    }
}