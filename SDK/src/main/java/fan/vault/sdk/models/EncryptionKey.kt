package fan.vault.sdk.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

data class EncryptionKeyResponse(val symmetricKey: String)

@Parcelize
data class EncryptionKeyRequest(
    val authSig: AuthSig,
    val accessConditions: List<AccessControlConditions>,
    val encryptedSymmetricKey: String
): Parcelable

@Parcelize
data class AccessControlConditions(
    val method: String,
    val params: List<String>,
    val pdaParams: List<String?>,
    val pdaInterface: PdaInterface,
    val pdaKey: String,
    val chain: String,
    val returnValueTest: ReturnValueTest
): Parcelable

@Parcelize
data class ReturnValueTest(
    val key: String,
    val comparator: String,
    val value: String
): Parcelable

@Parcelize
data class PdaInterface(
    val offset: Number,
    val fields: Map<String, Number>
): Parcelable