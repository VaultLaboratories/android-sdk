package fan.vault.sdk.models

import com.metaplex.lib.modules.nfts.models.NFT

data class NftWithMetadata(val nft: NFT, val metadata: JsonMetadataExt?)