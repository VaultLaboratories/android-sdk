package fan.vault.sdk.workers

import fan.vault.sdk.models.CreatorNFTProfile
import fan.vault.sdk.models.FeaturedDrop
import fan.vault.sdk.models.DropVariant
import fan.vault.sdk.models.Drop

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

data class VariantCreatorProfilesPair(val drop: Drop, val variant: DropVariant, val creatorNFTProfiles: List<CreatorNFTProfile>)