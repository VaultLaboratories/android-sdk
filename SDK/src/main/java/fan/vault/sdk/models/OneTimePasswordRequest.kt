package fan.vault.sdk.models

data class OneTimePasswordRequest(val userGuid: String, val provider: String, val appWallet: String, val token: String?, val nonce: String?)
