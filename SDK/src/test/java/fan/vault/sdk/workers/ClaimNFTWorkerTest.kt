package fan.vault.sdk.workers

import android.util.Base64
import com.solana.core.HotAccount
import fan.vault.sdk.models.SocialWalletResponse
import fan.vault.sdk.models.TransactionResponse
import io.mockk.every
import io.mockk.mockkStatic
import io.mockk.slot
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito
import org.mockito.kotlin.whenever
import retrofit2.Response

class ClaimNFTWorkerTest {
    @Before
    fun `Bypass android_util_Base64 to java_util_Base64`() {
        mockkStatic(Base64::class)
        val arraySlot = slot<ByteArray>()

        every {
            Base64.encodeToString(capture(arraySlot), Base64.DEFAULT)
        } answers {
            java.util.Base64.getEncoder().encodeToString(arraySlot.captured)
        }

        val stringSlot = slot<String>()
        every {
            Base64.decode(capture(stringSlot), Base64.DEFAULT)
        } answers {
            java.util.Base64.getDecoder().decode(stringSlot.captured)
        }
    }

    @Test
    fun shouldRetrieveAndSubmitClaimTransaction() {
        val mint = HotAccount().publicKey
        val seedWords =
            "transfer frown island economy raccoon champion wisdom talent tragic scrub kangaroo balcony twenty miracle soul bind abuse practice help crane betray enjoy artwork clever"

        val appWallet = HotAccount.fromMnemonic(seedWords.split(" "), "")
        val userEmailAddress = "Cthulhu@vault.fan"
        val otp = "666"

        val worker = instance()

        runBlocking {
            whenever(
                worker.proteusAPIWorker.getSocialToAppWalletClaimTransaction(
                    userEmailAddress = userEmailAddress,
                    appWallet = appWallet.publicKey.toBase58(),
                    mint = mint.toBase58(),
                    otp = otp
                )
            ).thenReturn(
                Response.success(TransactionResponse("AowfOvMIGMKpHdSMkJngzXiF+R1nhZpXl4ead8v9j2KMRNGWxw4ORAEMBxVDHauoPybbYzxE8DxwVkGNSlpHJA8AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAgAGDE1NcOheAvyv+bxRcoAAg+MRGtu2bkAm/5wdXQJ8rken1FULStGZ1asG/Yq4nf1D5fRiPugrD4fCDubSMsuHkdIayMAB688AuwbzoF9XnkQFo2FzCNJXzqL54DE6mJ3jcVhQyOsVuOXyU8XVGmOO2ep3vzbZGM4vgpLvjEcAM00J+V3TgVRf23jeOe/lnzkzO6KxIRRsdiBzWWuNQTu8yH/6ZEwlvNmeorQ162ZCz63Oge4CfDiNpiav1AqEyomoh3Fva1OSac1YjEGTGcfA31cfJcQU3+q9HrQZS+lxomSRdvMm5PB133e9DwijLEt6TSX1hyKy/MHeJpT5sgfx0Q6rTKN/L2kwSCbxyvNbI19PNeLYInltDmx6X1AtAMntzsjbHnHIO/C4BzQArccr6sxzJRGosQj9TDQA5uKlJYJ+C3BlsePRfEU4nVJ/awTDzVi4bHMaoP21SbbRvAP4KUYG3fbh12Whk9nL4UbO63msHLSF7V9bN5E6jPWFfv8AqS9th6tOQZlmjaIuFlZG82nyqa3jkyOF4l/umJZJMUYrAQcMAwQFAgABBggJCwsKQgxhNeMOztloLAAAAHE5N3A1TzE3VXRqWUVwRWI4UVd0Y0tXTi9hK0Q4MjI0OFhwOEdDNEFSczg9BgAAADEyMzQ1Ng=="))
            )

            runCatching {
                worker.claim(
                    nft = mint,
                    userEmailAddress = userEmailAddress,
                    appWallet = appWallet,
                    withOtp = otp
                )
            }
                .onFailure {
                    // We expect this one to fail as it has old blockhash
                    assertEquals(
                        "Transaction simulation failed: Blockhash not found",
                        it.message,
                    )
                }
        }

    }

    @Test
    fun shouldRetrieveClaimableNfts() {
        val userEmail = "antClaimableTest@vault.fan"
        val wallet = SocialWalletResponse(
            "EWueYv3bjYMDUGfq432rGzMQ3wAgb1MaYW7ZsSKpWHTZ",
            "0Zf/9zJPIniSKGMoA4d5usAIbu4FjQ8vZDwFcDqxAv0="
        )
        val worker = instance()

        runBlocking {
            whenever(worker.proteusAPIWorker.getSocialWalletAddress(userEmail)).thenReturn(wallet)
            val nfts = worker.getClaimableNfts(userEmail, includeCreatorData = false)
            val mintList = nfts.map { it?.nft?.mint?.toBase58() }
            assertTrue(mintList.contains("Dc6gWoQKK8WV1P91yJhvuuVuwpFd5y8y7hRhHovsso2Z"))
        }
    }

    private fun instance() =
        ClaimNFTWorker(Mockito.mock(ProteusAPIWorker::class.java), SolanaWorker(ProteusAPIWorker.create()))
}