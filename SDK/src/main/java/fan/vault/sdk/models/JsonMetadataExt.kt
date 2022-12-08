package fan.vault.sdk.models

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty

@JsonIgnoreProperties(ignoreUnknown = true)
data class JsonMetadataExt(
    val name: String,
    val description: String?,
    val symbol: String,
    val attributes: List<Trait>?,
    val type: DMCTypes,
    val image: String,
    var files: List<Any>,
    val items: List<JsonMetadataItemExt>,
    val links: List<Link>?
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
    var encryption: Encryption?,
    var metadata: Any?,
    val streamableUri: String?,
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class JsonMetadataAudioFileExt (
    val uri: String,
    val mime: String,
    val id: String,
    var encryption: Encryption?,
    var metadata: MusicMetadata?,
    val streamableUri: String?,
    val kbps: Number? // this is supposed to be mandatory but is not currently included in examples
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class MusicMetadata(
    val artists: List<String>,
    val displayArtistName: String?,
    val trackName: String,
    val trackNumber: Number?,
    val genre: List<String>?,
    val key: String?,
    val bpm: Number?,
    val tempo: Number?,
    val duration: String?,
    @JsonProperty("ISRC") val isrc: String?,
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

data class Encryption(
    val provider: EncryptionProvider,
    var providerData: Any,
)

data class LitProtocolData(
    val accessControlConditions: List<AccessControlConditions>,
    val encryptedSymmetricKey: String
)

data class Link(val uri: String, val rel: String)

data class Trait(@JsonProperty("trait_type") val traitType: String, val value: String)