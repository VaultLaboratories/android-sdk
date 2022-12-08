package fan.vault.sdk.models

import com.fasterxml.jackson.annotation.JsonProperty

enum class DMCTypes {
    @JsonProperty("dmc/single")
    SINGLE,

    @JsonProperty("dmc/ep")
    EP,

    @JsonProperty("dmc/album")
    ALBUM;
}