package fan.vault.sdk.models

import com.fasterxml.jackson.databind.annotation.JsonSerialize

data class EncryptionKeyResponse(val symmetricKey: String)

data class EncryptionKeyRequest(
    val authSig: AuthSig,
    val accessConditions: List<AccessControlConditions>,
    val encryptedSymmetricKey: String
)

//@JsonSerialize
data class AccessControlConditions(
    val method: String,
    val params: List<String>,
    val pdaParams: List<String?>,
    val pdaInterface: PdaInterface,
    val pdaKey: String,
    val chain: String,
    val returnValueTest: ReturnValueTest
)

//@JsonSerialize
data class ReturnValueTest(val key: String, val comparator: String, val value: String)

//@JsonSerialize
data class PdaInterface(val offset: Number, val fields: Map<String, Number>)