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
            whenever(mockProteusApi.getCollectionCreatorProfile(collectionMint)).thenReturn(
                listOf(
                    mock(CreatorNFTProfile::class.java)
                )
            )
            val storeWorker = StoreWorker(mockProteusApi)
            assertEquals(1, storeWorker.getFeaturedDrops().size)
        }
    }

    @Test
    fun getDevFeaturedDrops_returnsSomeDrops() {
        runBlocking {
            val storeWorker = StoreWorker(ProteusAPIWorker.create())
            val featuredDrops = storeWorker.getFeaturedDrops()
            assertEquals(true, featuredDrops.isNotEmpty())
        }
    }

    companion object {
        const val collectionMint = "collectionMintId"
    }

    private fun createJsonDropVariant() = DropVariant(
        "candyMachine", collectionMint, 999,
        JsonMetadataExt(
            "name",
            "description",
            "symbol",
            listOf(),
            DMCTypes.SINGLE,
            "image",
            1,
            emptyList(),
            emptyList(),
            emptyList(),
        ),
        100,
        50
    )

    private fun createJsonFeaturedDrop(variant: DropVariant) = Drop(
        "id",
        "store",
        DropData("slug", "", "", "", "", 100, 1670947003),
        listOf("creatorIds"),
        listOf(variant)
    )
}