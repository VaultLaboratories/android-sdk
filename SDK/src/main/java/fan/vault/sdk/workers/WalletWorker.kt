package fan.vault.sdk.workers

import org.p2p.solanaj.core.Account

class WalletWorker {
    private lateinit var wallet: StorageWorker

    fun generateWalletData(seeds: List<String>? = null): Account {
        return seeds?.let { Account.fromBip44MnemonicWithChange(it, "") } ?: Account()
    }

    fun loadWallet(): Account {
        wallet.loadWalletData()?.let { return it }
        val newWallet = generateWalletData()
        wallet.saveWallet(newWallet)
        return newWallet
    }
}