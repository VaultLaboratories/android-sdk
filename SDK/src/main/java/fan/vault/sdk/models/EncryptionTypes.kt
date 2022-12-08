package fan.vault.sdk.models

import com.fasterxml.jackson.annotation.JsonProperty

enum class EncryptionProvider {
    @JsonProperty("lit-protocol")
    LIT_PROTOCOL;
}