package fan.vault.sdk.models

import java.util.*
import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class FeaturedDrop(
    val id: String,
    val dropId: String,
    var candyMachineId: String,
    var name: String,
    var image: String?,
    var startDate: Date?,
    var price: Int,
    var creators: List<CreatorNFTProfile>,
    var type: DMCTypes,
): Parcelable {

    companion object {
        fun create(
            startDateSeconds: Long,
            dropId: String,
            variant: JsonDropVariant,
            creators: List<CreatorNFTProfile>
        ) =
            FeaturedDrop(
                id = variant.candyMachine,
                dropId = dropId,
                candyMachineId = variant.candyMachine,
                name = variant.collectionMetadata.name,
                image = variant.collectionMetadata.image,
                startDate = Date(startDateSeconds * 1000),
                price = variant.priceUsdCents,
                creators = creators,
                type = variant.collectionMetadata.attributes?.firstOrNull { it.traitType == "type" }?.value?.let {
                    DMCTypes.valueOf(it)
                } ?: DMCTypes.SINGLE)
    }
}