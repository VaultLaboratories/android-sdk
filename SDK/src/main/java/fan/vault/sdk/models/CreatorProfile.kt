package fan.vault.sdk.models

data class CreatorProfileButThisTimeItIsFromTheBlockChainBecauseThatIsCooler(
    val name: String?,
    val description: String?,
    val imageUrl: String?,
    val bannerUrl: String?,
    val socialLinks: List<SocialNetwork>? = null,
)

data class SocialNetwork(val socialNetwork: String, val uri: String)