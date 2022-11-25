package fan.vault.sdk.workers

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import com.solana.core.HotAccount

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

    //TODO: add seed phrase, email address

    fun saveWallet(wallet: Pair<List<String>, HotAccount>) {
        with(sharedPreferences.edit()) {
            putString(VAULT_WALLET_PUBLIC_KEY, wallet.second.publicKey.toBase58())
            putString(VAULT_WALLET_SEED_PHRASE, wallet.first.joinToString(","))
            apply()
        }
    }

    fun removeWalletData() {
        with(sharedPreferences.edit()) {
            remove(VAULT_WALLET_PUBLIC_KEY)
            remove(VAULT_WALLET_SEED_PHRASE)
            apply()
        }
    }

    fun loadWalletData(): HotAccount? {
        with(sharedPreferences) {
            val seedPhrase = getString(VAULT_WALLET_SEED_PHRASE, null)
            return when (seedPhrase != null) {
                true -> HotAccount.fromMnemonic(seedPhrase.split(","), "")
                false -> null
            }
        }
    }

    fun saveOtp(otp: String) {
        with(sharedPreferences.edit()) {
            putString(VAULT_USER_DEVICE_OTP, otp)
            apply()
        }
    }

    fun loadOtp(): String? = sharedPreferences.getString(VAULT_USER_DEVICE_OTP, null)

    fun clearAllStoredInfo() = sharedPreferences.edit().clear().apply()

    companion object {
        internal const val PREF_FILE_NAME = "vault_sdk_pref_file"

        internal const val VAULT_WALLET_PUBLIC_KEY = "VAULT_WALLET_PUBLIC_KEY"
        internal const val VAULT_WALLET_SEED_PHRASE = "VAULT_WALLET_SEED_PHRASE"
        internal const val VAULT_USER_DEVICE_OTP = "VAULT_USER_DEVICE_OTP"
    }
}
