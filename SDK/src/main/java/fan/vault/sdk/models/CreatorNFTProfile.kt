package fan.vault.sdk.models

data class CreatorNFTProfile(
    val name: String,
    val symbol: String,
    val slug: String,
    val verified: Boolean,
    val image: String,
    val description: String,
    val files: List<NFTFile>,
    val links: List<CreatorLink>
)

data class NFTFile(
    val id: String,
    val uri: String,
    val mime: String
)

data class CreatorLink(
    val network: String,
    val uri: String
)