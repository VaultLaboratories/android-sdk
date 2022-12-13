package fan.vault.sdk.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class AuthSig(
    val sig: String,
    val derivedVia: String = "solana.signMessage",
    val signedMessage: String,
    val address: String
): Parcelable
