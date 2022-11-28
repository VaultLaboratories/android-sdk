package fan.vault.sdk.models

data class DecryptedContent(
    val type: String?,
    val uri: String?,
    val mimetype: String?,
    val name: String?,
    val decryptedBytes: ByteArray?
)
