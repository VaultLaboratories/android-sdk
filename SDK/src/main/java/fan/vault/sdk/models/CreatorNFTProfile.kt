package fan.vault.sdk.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class CreatorNFTProfile(
    val name: String,
    val symbol: String,
    val slug: String,
    val verified: Boolean,
    val image: String,
    val description: String,
    val files: List<NFTFile>,
    val links: List<CreatorLink>
): Parcelable

@Parcelize
data class NFTFile(
    val id: String,
    val uri: String,
    val mime: String
): Parcelable

@Parcelize
data class CreatorLink(
    val network: String,
    val uri: String
): Parcelable