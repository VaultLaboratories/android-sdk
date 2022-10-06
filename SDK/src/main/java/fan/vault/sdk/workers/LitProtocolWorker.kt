package fan.vault.sdk.workers

import fan.vault.sdk.models.AuthSig
import org.bouncycastle.util.encoders.Hex
import org.p2p.solanaj.utils.TweetNaclFast.Signature
import java.text.SimpleDateFormat
import java.util.*

class LitProtocolWorker(val encryptionWorker: EncryptionWorker) {

    val AUTH_SIGNATURE_BODY = "I am creating an account to use Lit Protocol at "

    fun genAuthSig(): AuthSig {
        val now =
            SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss:mmm'Z'").format(Date(System.currentTimeMillis()))
        val body = AUTH_SIGNATURE_BODY.plus(now)
        val wallet = encryptionWorker.generateWalletData()

        val data = body.toByteArray()
        val signature =
            Signature(wallet.publicKey.toByteArray(), wallet.secretKey).detached(data)
        val hexSig = Hex.toHexString(signature)

        return AuthSig(hexSig, "solana.signMessage", body, wallet.publicKey.toBase58())
    }
}