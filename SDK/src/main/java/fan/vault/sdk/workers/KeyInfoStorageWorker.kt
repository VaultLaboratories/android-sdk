package fan.vault.sdk.workers

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import fan.vault.sdk.models.WalletData

class KeyInfoStorageWorker constructor(applicationContext: Context) {

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

    fun saveWalletData(walletData: WalletData) {
        with(sharedPreferences.edit()) {
            putString(VAULT_WALLET_PUBLIC_KEY, walletData.publicKey)
            putString(VAULT_WALLET_PRIVATE_KEY, walletData.privateKey)
            apply()
        }
    }

    fun removeWalletData() {
        with(sharedPreferences.edit()) {
            remove(VAULT_WALLET_PUBLIC_KEY)
            remove(VAULT_WALLET_PRIVATE_KEY)
            apply()
        }
    }

    fun loadWalletData(): WalletData? {
        with(sharedPreferences) {
            val publicKey = getString(VAULT_WALLET_PUBLIC_KEY, null)
            val privateKey = getString(VAULT_WALLET_PRIVATE_KEY, null)
            return when (publicKey != null && privateKey != null) {
                true -> WalletData(publicKey, privateKey)
                else -> null
            }
        }
    }

    fun clearAllStoredInfo() = sharedPreferences.edit().clear().apply()

    companion object {
        internal const val PREF_FILE_NAME = "vault_sdk_pref_file"

        internal const val VAULT_WALLET_PUBLIC_KEY = "VAULT_WALLET_PUBLIC_KEY"
        internal const val VAULT_WALLET_PRIVATE_KEY = "VAULT_WALLET_PRIVATE_KEY"
    }
}
