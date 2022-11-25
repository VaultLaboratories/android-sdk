package fan.vault.sdk

import android.content.Context
import android.media.MediaPlayer
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.metaplex.lib.modules.nfts.models.NFT
import fan.vault.sdk.models.JsonMetadataExt
import fan.vault.sdk.models.NftWithMetadata
import fan.vault.sdk.models.SocialWalletResponse
import fan.vault.sdk.workers.*

import io.reactivex.rxjava3.core.Single
import kotlinx.coroutines.runBlocking
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.util.concurrent.Executors
import java.util.concurrent.Future

object Vault {

    private var applicationContext: Context? = null
    private lateinit var storageWorker: StorageWorker
    private lateinit var walletWorker: WalletWorker
    private val litProtocolWorker by lazy { LitProtocolWorker(walletWorker) }
    private val proteusAPIWorker by lazy { ProteusAPIWorker.create() }
    private val proteusAPIWorkerCoroutines by lazy { ProteusAPIWorkerCoroutines.create() }
    private val solanaWorker by lazy { SolanaWorker() }

    private val executor = Executors.newFixedThreadPool(10)

    val mediaPlayer = MediaPlayer()

    fun initialize(context: Context) {
        applicationContext = context
        storageWorker = StorageWorker(context)
        walletWorker = WalletWorker(storageWorker)
    }

    fun initiateClaimNFTsLinkedTo(emailAddress: String) {}

    fun getAppWalletPublicKey(): String = walletWorker.loadWallet().publicKey.toString()

    fun decryptFile(): String {
        val url = "https://arweave.net/zPUcS81IIrJQ0arZjBBoAyOW_bG1PSKxKXPNEL2eKa0"
        val symmetricKey = "54d61c43b9832ce5652b09404ceddfae93603004aa4684a86bf64bcbdf9e6a16"

        val data = fetchEncryptedData(url).get()

        val decryptedFile = litProtocolWorker.decryptWithSymmetricKey(data, symmetricKey)

        val tempMp3: File = File.createTempFile("kurchina", "mp3")
        tempMp3.deleteOnExit()
        val fos = FileOutputStream(tempMp3)
        fos.write(decryptedFile)
        fos.close()

        val fis = FileInputStream(tempMp3)
        mediaPlayer.setDataSource(fis.fd)
        mediaPlayer.prepare()
        return "No. Tracks: ${mediaPlayer.trackInfo.size}.\n"
    }

    fun playPauseMedia() {
        if (mediaPlayer.isPlaying) {
            mediaPlayer.pause()
        } else {
            mediaPlayer.start()
        }
    }

    fun fetchEncryptedData(url: String): Future<ByteArray> {
        val client = OkHttpClient()
        val request = Request.Builder()
            .url(url)
            .get()
            .build()
        return executor.submit<ByteArray> {
            client
                .newCall(request)
                .execute()
                .body?.bytes()
        }
    }

    fun getOtp(): String? = storageWorker.loadOtp()

    fun saveOtp(otp: String) = storageWorker.saveOtp(otp)

    fun getSocialWallet(emailAddress: String): Single<SocialWalletResponse> = proteusAPIWorker.getSocialWalletAddress(emailAddress)

    suspend fun getSocialWallet2(emailAddress: String): SocialWalletResponse = proteusAPIWorkerCoroutines.getSocialWalletAddress(emailAddress)

    suspend fun getNftsForWalletAddress(walletAddress: String) = solanaWorker.listNFTsWithMetadata(walletAddress)
}