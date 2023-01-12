package fan.vault.sdk.workers

import fan.vault.sdk.models.*
import okhttp3.OkHttpClient
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import java.util.concurrent.TimeUnit

interface ProteusAPIWorker {

    @POST("/litprotocol/encryption-key")
    suspend fun getEncryptionKey(@Body body: EncryptionKeyRequest): EncryptionKeyResponse

    @POST("/mint/v2/otp")
    suspend fun requestOneTimePassword(@Body body: OneTimePasswordRequest): Response<String>

    @GET("/profiles/v2/social-wallet/{guid}/{provider}")
    suspend fun getSocialWalletAddress(@Path("guid") guid: String, @Path("provider") provider: AuthProviders): Response<SocialWalletResponse>

    @GET("/mint/v2/{guid}/{provider}/{appWallet}/{mint}/{otp}")
    suspend fun getSocialToAppWalletClaimTransaction(
        @Path("guid") guid: String,
        @Path("provider") provider: AuthProviders,
        @Path("appWallet") appWallet: String,
        @Path("mint") mint: String,
        @Path("otp") otp: String
    ): Response<TransactionResponse>

    @GET("/mint/{mint}/creator-profile")
    suspend fun getCreatorProfile(
        @Path("mint") mint: String
    ): List<CreatorNFTProfile>

    @GET("/mint/collection-mint/{collectionMint}/creator-profile")
    suspend fun getCollectionCreatorProfile(
        @Path("collectionMint") collectionMint: String
    ): List<CreatorNFTProfile>

    @GET("/stores/featured")
    suspend fun getFeaturedDrops(): List<Drop>

    @GET("/mint/v2/owned/by-social-wallet/{guid}/{provider}")
    suspend fun getSocialWalletMints(
        @Path("guid") guid: String,
        @Path("provider") provider: AuthProviders
    ): List<NftWithMetadata>

    companion object {
        private const val BASE_URL = "https://v0uusuz5j4.execute-api.us-east-2.amazonaws.com/"

        fun create(): ProteusAPIWorker {

            val client = OkHttpClient.Builder()
                .callTimeout(0, TimeUnit.SECONDS)
                .connectTimeout(10, TimeUnit.SECONDS)
                .readTimeout(60, TimeUnit.SECONDS)
                .writeTimeout(0, TimeUnit.SECONDS)
                .build()
            return Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(ProteusAPIWorker::class.java)
        }

    }
}