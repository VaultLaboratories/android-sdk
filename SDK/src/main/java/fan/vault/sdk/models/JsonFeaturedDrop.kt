package fan.vault.sdk.models

import com.fasterxml.jackson.annotation.JsonIgnoreProperties

@JsonIgnoreProperties(ignoreUnknown = true)
data class JsonFeaturedDrop(
    val id: String,
    val data: JsonFeaturedDropData,
    val variants: List<JsonDropVariant>
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class JsonFeaturedDropData(
    val creators: List<JsonCreator>,
    val startDate: String,//Change to Int after launcher changes
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class JsonDropVariant (
    val candyMachine: String,
    val priceUsdCents: String,//Change to Int after launcher changes
    val collectionMint: String,
    var collectionMetadata: JsonMetadataExt
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class JsonCreator (
    val profile: String?,
    val percentageShare: Int
)
