package fan.vault.sdk.workers

import fan.vault.sdk.models.Nft
import org.p2p.solanaj.core.PublicKey
import org.p2p.solanaj.rpc.RpcClient

class SolanaWorker (val proteusWorker: ProteusWorker) {
    val client = RpcClient(SOLANA_URL, 60)

    fun listNFTs(walletAddress: String): List<Nft> {
        val mints = client.api
            .getTokenAccountsByOwner(
                PublicKey(walletAddress),
                mapOf("programId" to TOKEN_PROGRAM_ID),
                emptyMap()
            )
            .value.orEmpty()
            .filter { it.account.data.parsed.info.tokenAmount.uiAmount.toLong() == 1L }
            .map { it.account.data.parsed.info.mint }

        return proteusWorker.getNFTsMetadata(mints)
    }

    companion object {
        //val solanaConnection = SolanaConnectionDriver(RPCEndpoint.mainnetBetaSolana)
        const val TOKEN_PROGRAM_ID =
            "TokenkegQfeZyiNwAJbNbGKPFXCWuBvf9Ss623VQ5DA" // program id owning metaplex created NFTs

        //TODO move it as param
        val SOLANA_URL =
            "https://fragrant-dawn-snow.solana-devnet.quiknode.pro/8dc589896709f8ee050dbe7ef4d012a32957f520/"
    }
}