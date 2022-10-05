package fan.vault.sdk.workers

import fan.vault.sdk.models.AuthSig
import org.p2p.solanaj.utils.TweetNaclFast.Signature
import java.util.*

class LitProtocolWorker(val encryptionWorker: EncryptionWorker) {

    val AUTH_SIGNATURE_BODY = "I am creating an account to use Lit Protocol at "

    fun genAuthSig(){
        val now = Date(System.currentTimeMillis()).toString()
        val body = AUTH_SIGNATURE_BODY.plus(now)

        val wallet = encryptionWorker.generateWalletData()

        val data = body.toByteArray()
        val signature = Signature(wallet.publicKey.toByteArray(), wallet.privateKey.toByteArray()).sign(data)
        val hexSig = String.format("%02X", signature)

        val authSig = AuthSig(hexSig, "solana.signMessage", body, wallet.publicKey)
    }
}