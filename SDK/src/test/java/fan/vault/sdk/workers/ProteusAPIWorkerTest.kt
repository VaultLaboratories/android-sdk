package fan.vault.sdk.workers

import com.google.gson.GsonBuilder
import fan.vault.sdk.models.APIError
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.Test

class ProteusAPIWorkerTest {

    private val gson = GsonBuilder().create()

    @Test
    fun shouldGetWrongOTPError() {
        val email = "ant@vault.fan"
        val appWallet = "CE2yUfCQGUzMi7imdLcGeqnfGBGz2zsmKFtU2d7pTfUC"
        val mint = "7SQUL7GcLCBvey4KxmUZEdVR5P23VLtVBhebVzfMN5G3"
        val otp = "fefa5fe4-6551-47a0-82ca-f4c6c7f35"

        runBlocking {
            val txResp =
                instance().getSocialToAppWalletClaimTransaction(email, appWallet, mint, otp)
            val apiError = gson.fromJson(txResp.errorBody()?.string(), APIError::class.java)
            assertFalse(txResp.isSuccessful)
            assertEquals("Incorrect OTP or Email", apiError.error)
        }
    }

    @Test
    fun shouldGetCorrectOTPResult() {
        val email = "ant@vault.fan"
        val appWallet = "CE2yUfCQGUzMi7imdLcGeqnfGBGz2zsmKFtU2d7pTfUC"
        val mint = "7SQUL7GcLCBvey4KxmUZEdVR5P23VLtVBhebVzfMN5G3"
        val otp = "fefa5fe4-6551-47a0-82ca-f4c6c7f35ccc"

        runBlocking {
            val txResp =
                instance().getSocialToAppWalletClaimTransaction(email, appWallet, mint, otp)
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

    private fun instance() =
        ProteusAPIWorker.create()
}