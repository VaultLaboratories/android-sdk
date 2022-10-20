package fan.vault.sdk.workers

import fan.vault.sdk.models.*
import io.reactivex.rxjava3.core.Single
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import java.util.concurrent.TimeUnit

interface ProteusAPIWorker {

    @POST("/litprotocol/encryption-key")
    fun getEncryptionKey(@Body body: EncryptionKeyRequest): Single<EncryptionKeyResponse>

    @POST("/mint/otp")
    fun requestOneTimePassword(@Body body: OneTimePasswordRequest): Single<String>

    @GET("/profiles/social-wallet/{userEmailAddress}")
    fun getSocialWalletAddress(@Path("userEmailAddress") userEmailAddress: String): Single<SocialWalletResponse>

    @GET("/mint/{userEmailAddress}/{appWallet}/{mint}/{otp}")
    fun getSocialToAppWalletClaimTransaction(
        @Path("userEmailAddress") userEmailAddress: String,
        @Path("appWallet") appWallet: String,
        @Path("mint") mint: String,
        @Path("otp") otp: String
    ): Single<TransactionResponse>

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
                .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
                .build()
                .create(ProteusAPIWorker::class.java)
        }
    }
}