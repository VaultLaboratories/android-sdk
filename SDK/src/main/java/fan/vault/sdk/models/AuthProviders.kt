package fan.vault.sdk.models

import android.os.Parcelable
import com.fasterxml.jackson.annotation.JsonProperty
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
class AuthProviders : Parcelable {
    companion object{
        val password = "password"
        val google = "google.com"
        val apple = "apple.com"
    }
}