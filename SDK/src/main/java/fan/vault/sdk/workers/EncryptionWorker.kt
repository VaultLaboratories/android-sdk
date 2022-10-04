package fan.vault.sdk.workers

import fan.vault.sdk.models.WalletData
import org.bitcoinj.core.Base58
import org.p2p.solanaj.core.*

class EncryptionWorker {

    fun generateWalletData(): WalletData {
        //TODO: generate random list of words
        val randomSeedWords = listOf("seed", "words", "here")
        val account = Account
            .fromBip44MnemonicWithChange(randomSeedWords, "")
        return WalletData(account.publicKey.toBase58(), Base58.encode(account.secretKey))
    }
}