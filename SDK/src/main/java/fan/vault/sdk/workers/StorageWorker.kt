package fan.vault.sdk.workers

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import org.bitcoinj.core.Base58
import org.p2p.solanaj.core.Account

class StorageWorker constructor(applicationContext: Context) {

    private val mainKey = MasterKey.Builder(applicationContext)
        .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
        .build()
    private val sharedPreferences: SharedPreferences = EncryptedSharedPreferences.create(
        applicationContext,
        PREF_FILE_NAME,
        mainKey,
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )

    //TODO: add seed phrase, email address, OTP

    fun saveWallet(wallet: Account) {
        with(sharedPreferences.edit()) {
            putString(VAULT_WALLET_PUBLIC_KEY, wallet.publicKey.toBase58())
            putString(VAULT_WALLET_SECRET_KEY, Base58.encode(wallet.secretKey).toString())
            apply()
        }
    }

    fun removeWalletData() {
        with(sharedPreferences.edit()) {
            remove(VAULT_WALLET_PUBLIC_KEY)
            remove(VAULT_WALLET_SECRET_KEY)
            apply()
        }
    }

    fun loadWalletData(): Account? {
        with(sharedPreferences) {
            val publicKey = getString(VAULT_WALLET_PUBLIC_KEY, null)
            val secretKey = getString(VAULT_WALLET_SECRET_KEY, null)
            return when (publicKey != null && secretKey != null) {
                true -> Account(Base58.decode(secretKey))
                false -> null
            }
        }
    }

    fun clearAllStoredInfo() = sharedPreferences.edit().clear().apply()

    companion object {
        internal const val PREF_FILE_NAME = "vault_sdk_pref_file"

        internal const val VAULT_WALLET_PUBLIC_KEY = "VAULT_WALLET_PUBLIC_KEY"
        internal const val VAULT_WALLET_SECRET_KEY = "VAULT_WALLET_SECRET_KEY"
    }
}
