package fan.vault.sdk.models

import android.os.Parcelable
import com.fasterxml.jackson.annotation.JsonProperty
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
enum class DMCTypes : Parcelable {
    @JsonProperty("dmc/single")
    @SerializedName("dmc/single")
    SINGLE,

    @JsonProperty("dmc/ep")
    @SerializedName("dmc/ep")
    EP,

    @JsonProperty("dmc/album")
    @SerializedName("dmc/album")
    ALBUM;
}