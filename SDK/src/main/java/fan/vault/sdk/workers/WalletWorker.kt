package fan.vault.sdk.workers

import org.bitcoinj.crypto.MnemonicCode
import org.p2p.solanaj.core.Account
import java.util.*

class WalletWorker(val storageWorker: StorageWorker) {

    fun generateWalletData(seeds: List<String>? = null): Pair<List<String>, Account> {
        val b = ByteArray(16) // 128 bits is 12 seed words
        Random().nextBytes(b)
        val generatedSeeds = MnemonicCode.INSTANCE.toMnemonic(b)
        return seeds?.let { it to Account.fromBip44MnemonicWithChange(it, "") }
            ?: (generatedSeeds to Account.fromBip44MnemonicWithChange(generatedSeeds, ""))
    }

    fun loadWallet(): Account {
        storageWorker.loadWalletData()?.let { return it }
        val newWallet = generateWalletData()
        storageWorker.saveWallet(newWallet)
        return newWallet.second
    }
}