package fan.vault.sdk.workers

import fan.vault.sdk.models.AccessControlConditions
import fan.vault.sdk.models.AuthSig
import fan.vault.sdk.models.EncryptionKeyRequest
import fan.vault.sdk.models.EncryptionKeyResponse
import io.reactivex.rxjava3.core.Single
import okio.ByteString.Companion.decodeHex
import org.bouncycastle.util.encoders.Hex
import java.text.SimpleDateFormat
import java.util.*
import javax.crypto.Cipher
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec

class LitProtocolWorker(val walletWorker: WalletWorker) {

    val AUTH_SIGNATURE_BODY = "I am creating an account to use Lit Protocol at "

    val api = ProteusAPIWorker.create()

    fun genAuthSig(): AuthSig {
        val now =
            SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss:mmm'Z'").format(Date(System.currentTimeMillis()))
        val body = AUTH_SIGNATURE_BODY.plus(now)
        val wallet = walletWorker.loadWallet()

        val data = body.toByteArray()
        val signature = wallet.sign(data)
        val hexSig = Hex.toHexString(signature)

        return AuthSig(hexSig, "solana.signMessage", body, wallet.publicKey.toBase58())
    }

    suspend fun getSymmetricKey(
        authSig: AuthSig,
        accessConditions: List<AccessControlConditions>,
        encryptedSymmetricKey: String
    ): EncryptionKeyResponse {
        val encryptionKeyRequest =
            EncryptionKeyRequest(authSig, accessConditions, encryptedSymmetricKey)
        return api.getEncryptionKey(encryptionKeyRequest)
    }

    /**
     * Decrypt an encrypted ByteArray with a symmetric key.  Uses AES-CBC
     * @param {ByteArray} encryptedBlob The encrypted blob that should be decrypted
     * @param {String} symmKey The symmetric key
     * @returns {ByteArray} The decrypted blob
     */
    fun decryptWithSymmetricKey(encryptedBlob: ByteArray, symmKey: String): ByteArray {
        val recoveredIv = encryptedBlob.slice(0..15).toByteArray()
        val encryptedZipArrayBuffer = encryptedBlob.slice(16..encryptedBlob.lastIndex).toByteArray()
        val byteString = symmKey.decodeHex().toByteArray()
        val secretKeySpec = SecretKeySpec(byteString, "AES")
        val cipher: Cipher =
            Cipher.getInstance("AES/CBC/PKCS5PADDING") //Possibly want "AES/CBC/NoPadding"
        cipher.init(Cipher.DECRYPT_MODE, secretKeySpec, IvParameterSpec(recoveredIv))
        return cipher.doFinal(encryptedZipArrayBuffer)
    }
}