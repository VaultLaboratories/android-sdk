package fan.vault.sdk.workers

import fan.vault.sdk.models.AccessControlConditions
import fan.vault.sdk.models.AuthSig
import fan.vault.sdk.models.EncryptionKeyRequest
import fan.vault.sdk.models.EncryptionKeyResponse
import org.bouncycastle.util.encoders.Hex
import org.p2p.solanaj.utils.TweetNaclFast.Signature
import java.text.SimpleDateFormat
import java.util.*


class LitProtocolWorker(val walletWorker: WalletWorker) {

    val AUTH_SIGNATURE_BODY = "I am creating an account to use Lit Protocol at "

    val api = ProteusAPIWorker.create()

    fun genAuthSig(): AuthSig {
        val now =
            SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss:mmm'Z'").format(Date(System.currentTimeMillis()))
        val body = AUTH_SIGNATURE_BODY.plus(now)
        val wallet = walletWorker.loadWallet()

        val data = body.toByteArray()
        val signature =
            Signature(wallet.publicKey.toByteArray(), wallet.secretKey).detached(data)
        val hexSig = Hex.toHexString(signature)

        return AuthSig(hexSig, "solana.signMessage", body, wallet.publicKey.toBase58())
    }

    fun getSymmetricKey(
        accessConditions: List<AccessControlConditions>,
        encryptedSymmetricKey: String
    ): EncryptionKeyResponse {
        val authSig = genAuthSig()
        val encryptionKeyRequest =
            EncryptionKeyRequest(authSig, accessConditions, encryptedSymmetricKey)
        return api.getEncryptionKey(encryptionKeyRequest).blockingGet()
    }

}