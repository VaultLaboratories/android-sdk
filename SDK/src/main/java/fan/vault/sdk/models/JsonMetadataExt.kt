package fan.vault.sdk.models

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.metaplex.lib.modules.nfts.models.*
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
@JsonIgnoreProperties(ignoreUnknown = true)
data class JsonMetadataExt(
    val name: String?,
    val symbol: String?,
    val description: String?,
    val seller_fee_basis_points: Double?,
    val image: String?,
    val external_url: String?,
    val attributes: List<JsonMetadataAttributeExt>?,
    val properties: JsonMetadataPropertiesExt?,
)

@JsonClass(generateAdapter = true)
@JsonIgnoreProperties(ignoreUnknown = true)
data class JsonMetadataPropertiesExt(
    val creators: List<JsonMetadataCreator>?,
    val files: List<JsonMetadataFileExt>?,
    val benefits: List<String>?
)

@JsonClass(generateAdapter = false)
data class JsonMetadataAttributeExt(
    val display_type: String?,
    val trait_type: String?,
    val value: Any?
)

@JsonClass(generateAdapter = false)
@JsonIgnoreProperties(ignoreUnknown = true)
data class JsonMetadataFileExt(
    val type: String?,
    val uri: String?,
    val mimetype: String?,
    val name: String?,
    val encryptedSymmetricKey: String?,
    val accessControlConditions: List<AccessControlConditions>?
)