package fan.vault.sdk.workers

import com.google.gson.GsonBuilder
import fan.vault.sdk.models.APIError
import fan.vault.sdk.models.AuthProviders
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.Ignore
import org.junit.Test

class ProteusAPIWorkerTest {

    private val gson = GsonBuilder().create()

    @Test
    fun shouldGetSocialWalletAddress() {
        val email = "ant@vault.fan"
        val authProvider = AuthProviders.password
        val socialWalletAddress = "58xnFZDoFJoDZNg1nKkBVoqbGQ7nNcqscbJXKwmSvzZS"

        runBlocking {
            val txResp =
                instance().getSocialWalletAddress(email, authProvider)
            assertTrue(txResp.isSuccessful)
            assertEquals(socialWalletAddress, txResp.body()?.wallet)
        }
    }

    @Test
    fun shouldGetWrongOTPError() {
        val email = "ant@vault.fan"
        val authProvider = AuthProviders.password
        val appWallet = "CE2yUfCQGUzMi7imdLcGeqnfGBGz2zsmKFtU2d7pTfUC"
        val mint = "7SQUL7GcLCBvey4KxmUZEdVR5P23VLtVBhebVzfMN5G3"
        val otp = "fefa5fe4-6551-47a0-82ca-f4c6c7f35"

        runBlocking {
            val txResp =
                instance().getSocialToAppWalletClaimTransaction(email, authProvider, appWallet, mint, otp)
            val apiError = gson.fromJson(txResp.errorBody()?.string(), APIError::class.java)
            assertFalse(txResp.isSuccessful)
            assertEquals("Incorrect OTP or Email", apiError.error)
        }
    }

    @Test
    fun shouldGetCorrectOTPResult() {
        val email = "anthony@vault.fan"
        val authProvider = AuthProviders.password
        val appWallet = "CE2yUfCQGUzMi7imdLcGeqnfGBGz2zsmKFtU2d7pTfUC"
        val mint = "4MgEan8rggRQ41pW3w6VPvZKGvKARUg3iJh2bf9eoHVk"
        val otp = "49d6ef66-eaea-4e83-b7f6-866930b7fdaf"

        runBlocking {
            val txResp =
                instance().getSocialToAppWalletClaimTransaction(email, authProvider, appWallet, mint, otp)
            assertTrue(txResp.isSuccessful)
        }
    }

    @Test
    fun shouldGetCreatorsForMintAddress() {
        val mintAddress = "3ojqGNDMAeNGugiaAgMM3chbVt6mHpy8E2S38PAeH7kb"

        runBlocking {
            val resp = instance().getCreatorProfile(mintAddress)
            assertTrue(resp.isNotEmpty())
        }
    }

    @Test
    fun shouldGetCreatorsForCollectionMintAddress() {
        val collectionMintAddress = "3QLZVcYGPRfvik2pXZVptTXnWYXy6H3Nz9wpEw5gxKwJ"

        runBlocking {
            val resp = instance().getCollectionCreatorProfile(collectionMintAddress)
            assertTrue(resp.isNotEmpty())
        }
    }

    @Test
    fun shouldGetFeaturedDrops() {
        runBlocking {
            val featuredDrops = instance().getFeaturedDrops()
            assertTrue(featuredDrops.isNotEmpty())
        }
    }

    @Test
    @Ignore // this will rely on some changes in proteus API to get working I think
    fun shouldGetSocialWalletTokens(){
        val email = "anthony+SocialWalletTokenTest@vault.fan"
        val authProvider = AuthProviders.password // mitn 9ZWdofCw5P42Ks14mMHbiZvXQwth8Ea7PNkZb2HJXtvJ

        runBlocking {
            val socialWalletTokenResp = instance().getSocialWalletMints(email, authProvider)
            assertTrue(socialWalletTokenResp.isNotEmpty())

        }
    }

    private fun instance() =
        ProteusAPIWorker.create()
}