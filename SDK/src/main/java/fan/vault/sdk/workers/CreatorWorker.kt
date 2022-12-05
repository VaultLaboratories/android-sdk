package fan.vault.sdk.workers

import fan.vault.sdk.models.NftWithMetadata

class CreatorWorker(val proteusAPIWorker: ProteusAPIWorker, val solanaWorker: SolanaWorker){

    //todo - feels like creating a worker for this one call is a bit heavy handed, but will there be other creator related functions to come?

    suspend fun getCreator(userEmailAddress: String, creatorAddress: String): NftWithMetadata? =
        proteusAPIWorker.getSocialWalletAddress(userEmailAddress).let {
            solanaWorker.getCreatorByAddress(it.wallet, creatorAddress)
        }
}