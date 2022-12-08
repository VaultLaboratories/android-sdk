package fan.vault.sdk.models

import com.fasterxml.jackson.annotation.JsonProperty

enum class ItemTypes {
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