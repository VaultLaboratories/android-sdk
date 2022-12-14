package fan.vault.sdk.workers

import fan.vault.sdk.models.CreatorNFTProfile
import fan.vault.sdk.models.FeaturedDrop
import fan.vault.sdk.models.JsonDropVariant
import fan.vault.sdk.models.JsonFeaturedDrop

class StoreWorker(val proteusAPIWorker: ProteusAPIWorker) {

    suspend fun getFeaturedDrops(): List<FeaturedDrop> =
        proteusAPIWorker.getFeaturedDrops()
            .map { drop ->
                drop.variants.map { VariantCreatorProfilesPair(drop, it, proteusAPIWorker.getCollectionCreatorProfile(it.collectionMint)) }
            }.fold(mutableListOf()) { output, listOfVariantData ->
                listOfVariantData.forEach {
                    output.add(FeaturedDrop.create(it.drop.data.startDate, it.drop.id, it.variant, it.creatorNFTProfiles))
                }
                output
            }
}

data class VariantCreatorProfilesPair(val drop: JsonFeaturedDrop, val variant: JsonDropVariant, val creatorNFTProfiles: List<CreatorNFTProfile>)