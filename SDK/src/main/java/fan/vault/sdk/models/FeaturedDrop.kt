package fan.vault.sdk.models

import java.util.*
import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class FeaturedDrop(
    val id: String,
    val dropId: String,
    var startDate: Date?,
    var creators: List<CreatorNFTProfile>,
    var variant: JsonDropVariant
) : Parcelable {

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
                startDate = Date(startDateSeconds * 1000),
                creators = creators,
                variant = variant
            )
    }
}