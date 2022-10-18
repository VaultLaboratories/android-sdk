package fan.vault.sdk

import android.util.Base64
import com.solana.core.SerializeConfig
import com.solana.core.Transaction
import fan.vault.sdk.models.TransactionResponse
import fan.vault.sdk.workers.SolanaWorker
import fan.vault.sdk.workers.WalletWorker
import io.mockk.every
import io.mockk.mockkStatic
import io.mockk.slot
import junit.framework.Assert.assertEquals
import junit.framework.Assert.assertTrue
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Ignore
import org.junit.Test
import java.util.concurrent.Executors
import java.util.concurrent.FutureTask

class SolanaWorkerTest {
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
    fun shouldReturnListNfts() {
        val worker = instance()

        runBlocking {
            val nfts = worker
                .listNFTsWithMetadata("FHreS1zRRqDKYfkZzoCKCPyxPNqwFFCky15qWpcvZJTT") // We might need to consider using different wallet for test

            assertTrue(nfts.size >= 3)
        }
    }

    @Test
    @Ignore
    fun shouldSignAndSendTransaction() {
        val executor = Executors.newFixedThreadPool(10)
        val worker = instance()
        val transactionResponse = TransactionResponse(
            hashTrx = "AowfOvMIGMKpHdSMkJngzXiF+R1nhZpXl4ead8v9j2KMRNGWxw4ORAEMBxVDHauoPybbYzxE8DxwVkGNSlpHJA8AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAgAGDE1NcOheAvyv+bxRcoAAg+MRGtu2bkAm/5wdXQJ8rken1FULStGZ1asG/Yq4nf1D5fRiPugrD4fCDubSMsuHkdIayMAB688AuwbzoF9XnkQFo2FzCNJXzqL54DE6mJ3jcVhQyOsVuOXyU8XVGmOO2ep3vzbZGM4vgpLvjEcAM00J+V3TgVRf23jeOe/lnzkzO6KxIRRsdiBzWWuNQTu8yH/6ZEwlvNmeorQ162ZCz63Oge4CfDiNpiav1AqEyomoh3Fva1OSac1YjEGTGcfA31cfJcQU3+q9HrQZS+lxomSRdvMm5PB133e9DwijLEt6TSX1hyKy/MHeJpT5sgfx0Q6rTKN/L2kwSCbxyvNbI19PNeLYInltDmx6X1AtAMntzsjbHnHIO/C4BzQArccr6sxzJRGosQj9TDQA5uKlJYJ+C3BlsePRfEU4nVJ/awTDzVi4bHMaoP21SbbRvAP4KUYG3fbh12Whk9nL4UbO63msHLSF7V9bN5E6jPWFfv8AqS9th6tOQZlmjaIuFlZG82nyqa3jkyOF4l/umJZJMUYrAQcMAwQFAgABBggJCwsKQgxhNeMOztloLAAAAHE5N3A1TzE3VXRqWUVwRWI4UVd0Y0tXTi9hK0Q4MjI0OFhwOEdDNEFSczg9BgAAADEyMzQ1Ng=="
        )

        val seedWords =
            "transfer frown island economy raccoon champion wisdom talent tragic scrub kangaroo balcony twenty miracle soul bind abuse practice help crane betray enjoy artwork clever"

        val decoded = Base64.decode(transactionResponse.hashTrx, Base64.DEFAULT)
        val transaction = Transaction.from(decoded)

        transaction.instructions.forEach {
            it.keys.forEach{
                println(it.publicKey.toBase58())
            }
        }

        val serialized = transaction.serialize(SerializeConfig(false, true))
        val base64Trx: String = Base64.encodeToString(serialized, Base64.DEFAULT)

        val decoded2 = Base64.decode(base64Trx, Base64.DEFAULT)
        val transaction2 = Transaction.from(decoded2)

        val serialized2 = transaction2.serialize(SerializeConfig(false, false))
        val base64Trx2: String = Base64.encodeToString(serialized2, Base64.DEFAULT)

        println("=========================")
        transaction2.instructions.forEach {
            it.keys.forEach{
                println(it.publicKey.toBase58())
            }
        }

        assertEquals(base64Trx, base64Trx2)
        assertEquals(base64Trx, transactionResponse.hashTrx)

//        val wallet = WalletWorker().generateWalletData(seedWords.split(" "))
//        worker.signAndSendTransaction(transactionResponse, wallet.second) {
//            println(it.getOrNull())
//        }


//        val s = Semaphore(1)
//
//        s.acquire()
//        response
//            .doOnError { s.release() }
//            .doOnSuccess { s.release() }
//        s.wait()

//        val disposable = response
//            .observeOn(Schedulers.computation())
//            .subscribe { x, y ->
////                assertEquals("", x)
//                assertEquals("", y.message)
//            }
//
//        disposable.wait()


    }

    private fun instance() = SolanaWorker()
}