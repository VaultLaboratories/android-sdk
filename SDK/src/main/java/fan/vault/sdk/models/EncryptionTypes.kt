package fan.vault.sdk.models

import android.os.Parcelable
import com.fasterxml.jackson.annotation.JsonProperty
import kotlinx.parcelize.Parcelize

@Parcelize
enum class EncryptionProvider: Parcelable {
    @JsonProperty("lit-protocol")
    LIT_PROTOCOL;
}