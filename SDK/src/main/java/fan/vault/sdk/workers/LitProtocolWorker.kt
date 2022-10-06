package fan.vault.sdk.workers

import fan.vault.sdk.models.AuthSig
import org.p2p.solanaj.utils.TweetNaclFast.Signature
import java.text.SimpleDateFormat
import java.util.*

class LitProtocolWorker(val encryptionWorker: EncryptionWorker) {

    val AUTH_SIGNATURE_BODY = "I am creating an account to use Lit Protocol at "

    fun genAuthSig(): AuthSig {
        val now = SimpleDateFormat("yyyy-MM-dd'T'HH:mm'Z'").format(Date(System.currentTimeMillis()))
        val body = AUTH_SIGNATURE_BODY.plus(now)

        val wallet = encryptionWorker.generateWalletData()

        val data = body.toByteArray()
        val signature =
            Signature(wallet.publicKey.toByteArray(), wallet.secretKey).sign(data)
        val hexSig =
            signature.asUByteArray().joinToString("") { it.toString(radix = 16).padStart(2, '0') }

        return AuthSig(hexSig, "solana.signMessage", body, wallet.publicKey.toBase58())
    }
}