package fan.vault.sdk.workers

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import fan.vault.sdk.models.AccessControlConditions
import fan.vault.sdk.models.AuthSig
import fan.vault.sdk.models.EncryptionKeyRequest
import fan.vault.sdk.models.EncryptionKeyResponse
import kotlinx.coroutines.runBlocking
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.bouncycastle.util.encoders.Hex
import org.p2p.solanaj.utils.TweetNaclFast.Signature
import java.net.URL
import java.text.SimpleDateFormat
import java.util.*


class LitProtocolWorker(val encryptionWorker: EncryptionWorker) {

    val AUTH_SIGNATURE_BODY = "I am creating an account to use Lit Protocol at "

    private val client = OkHttpClient()
    val mapper = jacksonObjectMapper()
    //val JSON: MediaType = "application/json; charset=utf-8".toMediaType()


    fun genAuthSig(): AuthSig {
        val now =
            SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss:mmm'Z'").format(Date(System.currentTimeMillis()))
        val body = AUTH_SIGNATURE_BODY.plus(now)
        val wallet = encryptionWorker.generateWalletData() //TODO add wallet read from storage here

        val data = body.toByteArray()
        val signature =
            Signature(wallet.publicKey.toByteArray(), wallet.secretKey).detached(data)
        val hexSig = Hex.toHexString(signature)

        return AuthSig(hexSig, "solana.signMessage", body, wallet.publicKey.toBase58())
    }

    fun getSymmetricKey(
        accessConditions: List<AccessControlConditions>,
        encryptedSymmetricKey: String
    ) {
        val authSig = genAuthSig()
        val encryptionKeyRequest =
            EncryptionKeyRequest(authSig, accessConditions, encryptedSymmetricKey)
        val body: RequestBody = mapper.writeValueAsString(encryptionKeyRequest).toRequestBody("application/json".toMediaType())
        println(mapper.writeValueAsString(encryptionKeyRequest))

        val request = Request.Builder()
            .url(URL("https://v0uusuz5j4.execute-api.us-east-2.amazonaws.com/litprotocol/encryption-key"))
            .post(body)
            .build()

        runBlocking {
            val response = client.newCall(request).execute()

            println(response)

            println(mapper.writeValueAsString(response.body))
        }

    }

}