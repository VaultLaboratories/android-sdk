package fan.vault.sdk.models

import android.os.Parcelable
import com.fasterxml.jackson.annotation.JsonProperty
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
enum class AuthProviders(val text: String) : Parcelable {
    @JsonProperty("google.com")
    @SerializedName("google.com")
    GOOGLE("google.com"),

    @JsonProperty("apple.com")
    @SerializedName("apple.com")
    APPLE("apple.com"),

    @JsonProperty("password")
    @SerializedName("password")
    EMAIL("password");
}