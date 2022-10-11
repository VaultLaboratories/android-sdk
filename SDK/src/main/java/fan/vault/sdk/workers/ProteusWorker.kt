package fan.vault.sdk.workers

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import fan.vault.sdk.models.Nft
import okhttp3.OkHttpClient
import okhttp3.Request

class ProteusWorker {
    private val metadataCache = mutableMapOf<String, Nft>()
    private val client = OkHttpClient()

    fun getNFTsMetadata(mints: List<String>): List<Nft> {
        println(mints)
        val notFetchedYet = mints.subtract(metadataCache.keys)

        getTokensMetadata(notFetchedYet)
            .forEach { nft -> metadataCache[nft.mint] = nft }

        return mints
            .mapNotNull { metadataCache[it] }
    }

    fun getTokensMetadata(mints: Iterable<String>): List<Nft> {
        val url = "$BASE_URL/mint/metadata?mints=${
            mints.joinToString(
                "&mints="
            )
        }"
        val request = Request.Builder()
            .url(url)
            .get()
            .build()

        return client
            .newCall(request)
            .execute()
            .body?.string()
            ?.let { jacksonObjectMapper().readValue(it) } ?: emptyList()
    }

    companion object {
//        val BASE_URL = "https://v0uusuz5j4.execute-api.us-east-2.amazonaws.com"
        val BASE_URL = "http://localhost:8080"
    }

}