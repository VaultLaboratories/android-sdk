package fan.vault.sdk.workers

import org.bitcoinj.core.Base58
import org.bitcoinj.crypto.MnemonicCode
import org.p2p.solanaj.core.*
import org.p2p.solanaj.programs.SystemProgram
import org.p2p.solanaj.utils.bip32.wallet.HdKeyGenerator
import org.p2p.solanaj.utils.bip32.wallet.SolanaBip44
import javax.crypto.Cipher
import javax.crypto.spec.SecretKeySpec
import javax.crypto.spec.IvParameterSpec

class EncryptionWorker {

    /**
     * Decrypt an encrypted ByteArray with a symmetric key.  Uses AES-CBC
     * @param {ByteArray} encryptedBlob The encrypted blob that should be decrypted
     * @param {String} symmKey The symmetric key
     * @returns {ByteArray} The decrypted blob
     */
    fun decryptWithSymmetricKey(encryptedBlob: ByteArray, symmKey: String): ByteArray {
        val recoveredIv = encryptedBlob.slice(0..15).toByteArray()
        val encryptedZipArrayBuffer = encryptedBlob.slice(16..encryptedBlob.lastIndex).toByteArray()
        val secretKeySpec = SecretKeySpec(symmKey.toByteArray(), "AES")
        val cipher: Cipher =
            Cipher.getInstance("AES/CBC/PKCS5PADDING") //Possibly want "AES/CBC/NoPadding"
        cipher.init(Cipher.DECRYPT_MODE, secretKeySpec, IvParameterSpec(recoveredIv))
        return cipher.doFinal(encryptedZipArrayBuffer)
    }
}