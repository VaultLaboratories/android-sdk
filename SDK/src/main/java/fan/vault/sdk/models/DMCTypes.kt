package fan.vault.sdk.models

import android.os.Parcelable
import com.fasterxml.jackson.annotation.JsonProperty
import kotlinx.parcelize.Parcelize

@Parcelize
enum class DMCTypes: Parcelable {
    @JsonProperty("dmc/single")
    SINGLE,

    @JsonProperty("dmc/ep")
    EP,

    @JsonProperty("dmc/album")
    ALBUM;
}