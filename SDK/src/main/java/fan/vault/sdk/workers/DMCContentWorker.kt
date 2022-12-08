package fan.vault.sdk.workers

import android.util.Log
import fan.vault.sdk.models.EncryptionProvider
import fan.vault.sdk.models.JsonMetadataFileExt
import fan.vault.sdk.models.LitProtocolData
import okhttp3.HttpUrl.Companion.toHttpUrlOrNull
import okhttp3.OkHttpClient
import okhttp3.Request

class DMCContentWorker(val litProtocolWorker: LitProtocolWorker) {
    private val client = OkHttpClient()

    suspend fun decryptFile(file: JsonMetadataFileExt) =
        when (file.encryption?.provider) {
            EncryptionProvider.LIT_PROTOCOL -> decryptUsingLitProtocol(file)
            else -> throw Exception("File does not have recognised encryption provider")
        }

    private suspend fun decryptUsingLitProtocol(file: JsonMetadataFileExt): ByteArray {
        file.encryption?.providerData
            ?.takeIf { it is LitProtocolData }
            ?.let { it as LitProtocolData }
            ?.let {
                val symmKey = litProtocolWorker.getSymmetricKey(
                    litProtocolWorker.genAuthSig(),
                    it.accessControlConditions,
                    it.encryptedSymmetricKey
                )
                return litProtocolWorker.decryptWithSymmetricKey(
                    getEncryptedBytes(file),
                    symmKey.symmetricKey
                )
            } ?: throw Exception("No encryption provider data found")
    }

    private fun getEncryptedBytes(file: JsonMetadataFileExt) =
        file.uri.toHttpUrlOrNull()?.let {
            Request.Builder()
                .url(it)
                .get()
                .build().let { request ->
                    kotlin.runCatching {
                        client
                            .newCall(request)
                            .execute()
                            .body?.bytes()
                    }
                }.onFailure {
                    Log.i(
                        "Proteus",
                        "Unable to fetch from Arweave right now: ${it.localizedMessage}"
                    )
                }
                .getOrThrow()
        } ?: throw Exception("Incorrect URI format for DMC content")

}