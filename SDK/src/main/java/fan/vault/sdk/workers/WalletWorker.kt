package fan.vault.sdk.workers

import com.solana.core.Account
import com.solana.core.HotAccount
import org.bitcoinj.crypto.MnemonicCode
import java.util.*

class WalletWorker(private val storageWorker: StorageWorker) {

    fun generateWalletData(seeds: List<String>? = null): Pair<List<String>, HotAccount> {
        val b = ByteArray(16) // 128 bits is 12 seed words
        Random().nextBytes(b)
        val generatedSeeds = MnemonicCode.INSTANCE.toMnemonic(b)
        return seeds?.let { it to HotAccount.fromMnemonic(it, "") }
            ?: (generatedSeeds to HotAccount.fromMnemonic(generatedSeeds, ""))
    }

    fun loadWallet(): HotAccount {
        return storageWorker.loadWalletData() ?: run {
            val newWallet = generateWalletData()
            storageWorker.saveWallet(newWallet)
            newWallet.second
        }
    }
}