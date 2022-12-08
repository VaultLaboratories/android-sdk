package fan.vault.sdk.workers

import fan.vault.sdk.models.CreatorNFTProfile

class CreatorWorker(private val proteusAPIWorker: ProteusAPIWorker){

    suspend fun getCreators(mint: String): List<CreatorNFTProfile> =
        proteusAPIWorker.getCreatorProfile(mint)

    suspend fun getCreatorsForCollection(collectionMint: String): List<CreatorNFTProfile> =
        proteusAPIWorker.getCollectionCreatorProfile(collectionMint)

}