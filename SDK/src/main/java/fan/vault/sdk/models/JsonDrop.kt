package fan.vault.sdk.models

import android.os.Parcelable
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import kotlinx.parcelize.Parcelize

@JsonIgnoreProperties(ignoreUnknown = true)
data class JsonDrop(
    val id: String,
    val store: String,
    val data: JsonDropData,
    val creators: List<String>,
    val variants: List<JsonDropVariant>
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class JsonDropData(
    val slug: String,
    val name: String,
    val image: String,
    val description: String,
    val symbol: String,
    val sellerFeeBasisPoints: Long,
    val startDate: Long,
)

@JsonIgnoreProperties(ignoreUnknown = true)
@Parcelize
data class JsonDropVariant(
    val candyMachine: String,
    val collectionMint: String,
    val priceUsdCents: Int,
    var collectionMetadata: JsonMetadataExt,
    val itemsLoaded: Number,
    val itemsRemaining: Number
) : Parcelable
