package fan.vault.sdk.models

import android.os.Parcelable
import com.fasterxml.jackson.annotation.JsonProperty
import kotlinx.parcelize.Parcelize

@Parcelize
enum class ItemTypes: Parcelable {
    @JsonProperty("single-audio")
    SINGLE_AUDIO,

    @JsonProperty("single-image")
    SINGLE_IMAGE,

    @JsonProperty("single-video")
    SINGLE_VIDEO,

    @JsonProperty("multiple-audio")
    MULTI_AUDIO,

    @JsonProperty("multiple-image")
    MULTI_IMAGE,

    @JsonProperty("multiple-video")
    MULTI_VIDEO;
}