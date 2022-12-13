package fan.vault.sdk.models

import android.os.Parcelable
import com.metaplex.lib.modules.nfts.models.NFT
import kotlinx.parcelize.Parcelize
import kotlinx.parcelize.RawValue

@Parcelize
data class NftWithMetadata(
    val nft: @RawValue NFT,
    val metadata: JsonMetadataExt?,
    var creators: List<CreatorNFTProfile>? = null
): Parcelable