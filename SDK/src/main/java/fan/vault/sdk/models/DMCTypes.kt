package fan.vault.sdk.models

import android.os.Parcelable
import com.fasterxml.jackson.annotation.JsonGetter
import com.fasterxml.jackson.annotation.JsonProperty
import kotlinx.parcelize.Parcelize

@Parcelize
enum class DMCTypes(val text: String): Parcelable {
    SINGLE("dmc/single"),
    EP("dmc/ep"),
    ALBUM("dmc/album");

    companion object {
        fun fromText(text: String?) = values().firstOrNull { it.text == text }
    }
}