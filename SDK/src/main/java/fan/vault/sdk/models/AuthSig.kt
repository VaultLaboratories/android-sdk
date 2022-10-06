package fan.vault.sdk.models

data class AuthSig(
    val sig: String,
    val derivedVia: String = "solana.signMessage",
    val signedMessage: String,
    val address: String
)
