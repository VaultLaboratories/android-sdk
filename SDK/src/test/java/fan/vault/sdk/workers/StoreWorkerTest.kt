package fan.vault.sdk.workers

import fan.vault.sdk.models.*
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Test
import org.mockito.Mockito.*
import org.mockito.kotlin.whenever

class StoreWorkerTest {

    private val mockProteusApi = mock(ProteusAPIWorker::class.java)

    @Test
    fun givenThereAreNoDropsFromAPI_thenReturnEmptyList() {
        runBlocking {
            whenever(mockProteusApi.getFeaturedDrops()).thenReturn(emptyList())
            val storeWorker = StoreWorker(mockProteusApi)
            assertEquals(emptyList<FeaturedDrop>(), storeWorker.getFeaturedDrops())
        }
    }

    @Test
    fun givenThereIsADropFromAPI_thenReturnOneFeaturedDrop() {
        runBlocking {
            val jsonDropVariant = createJsonDropVariant()
            val jsonFeaturedDrop = createJsonFeaturedDrop(jsonDropVariant)
            val jsonFeaturedDropList = listOf(jsonFeaturedDrop)
            whenever(mockProteusApi.getFeaturedDrops()).thenReturn(jsonFeaturedDropList)
            whenever(mockProteusApi.getCollectionCreatorProfile(collectionMint)).thenReturn(listOf(mock(CreatorNFTProfile::class.java)))
            val storeWorker = StoreWorker(mockProteusApi)
            assertEquals(1, storeWorker.getFeaturedDrops().size)
        }
    }

    companion object {
        const val collectionMint = "collectionMintId"
    }

    private fun createJsonDropVariant() = JsonDropVariant("candyMachine", "999", collectionMint,
        JsonMetadataExt("name", "description", "symbol", listOf(), DMCTypes.SINGLE, "image", emptyList(), emptyList(), emptyList()))

    private fun createJsonFeaturedDrop(variant: JsonDropVariant) = JsonFeaturedDrop("id",
        JsonFeaturedDropData(listOf(), "1670947003"),
        listOf( variant))
}