package fan.vault.sdk.models

data class OneTimePasswordRequest(val userEmailAddress: String, val appWallet: String)

data class OneTimePasswordRequestV2(val userGuid: String, val provider: String, val appWallet: String, val token: String?, val nonce: String?)
