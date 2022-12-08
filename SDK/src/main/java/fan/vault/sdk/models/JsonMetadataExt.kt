package fan.vault.sdk.models

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty

@JsonIgnoreProperties(ignoreUnknown = true)
data class JsonMetadataExt(
    val name: String,
    val description: String?,
    val symbol: String,
    val attributes: List<Trait>? = null,
    val type: DMCTypes,
    val image: String,
    var files: List<JsonMetadataFileExt>,
    val items: List<JsonMetadataItemExt>,
    val links: List<Link>? = null
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class JsonMetadataItemExt(
    val id: String,
    val type: ItemTypes,
    val fileIds: List<String>,
    val index: Number,
    val preview: Boolean?,
    val name: String?,
    val description: String?
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class JsonMetadataFileExt (
    val uri: String,
    val mime: String,
    val id: String,
    val encryption: Encryption?,
    var metatdata: Any?,
    val streamableUri: String?,
    val kbps: Number?
)

data class MusicMetadata(
    val artists: List<String>,
    val displayArtistName: String,
    val trackName: String,
    val trackNumber: Number?,
    val genre: List<String>?,
    val key: String?,
    val bpm: Number?,
    val tempo: Number?,
    val duration: String?,
    val isrc: String?,
    val iswc: String?,
    val isni: String?,
    val iwi: String?,
    val workId: String?,
    val licenseType: String?,
    val licenseLink: String?,
    val creationDate: String?,
    val publisher: String?,
    val recordLable: String?,
    val explicit: Boolean?
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class Encryption(
    val provider: EncryptionProvider,
    val providerData: LitProtocolData,
)

data class LitProtocolData(
    val accessControlConditions: List<AccessControlConditions>,
    val encryptedSymmetricKey: String
)

data class Link(val uri: String, val rel: String)

data class Trait(@JsonProperty("trait_type") val traitType: String, val value: String)