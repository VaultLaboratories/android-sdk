package fan.vault.sdk.workers

import org.p2p.solanaj.core.*

class EncryptionWorker {

    fun generateWalletData() {

        val address = Account
            .fromBip44MnemonicWithChange(listOf("seed", "words", "here"), "")
            .publicKey.toBase58()
    }
}