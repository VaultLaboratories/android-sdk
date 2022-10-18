package fan.vault.sdk.solana

import com.solana.api.Api
import com.solana.models.RpcSendTransactionConfig

fun Api.sendRawTransaction(base64Encoded: String, onComplete: (Result<String>) -> Unit) {
    router.request("sendTransaction", listOf(base64Encoded, RpcSendTransactionConfig()), String::class.java, onComplete)
}