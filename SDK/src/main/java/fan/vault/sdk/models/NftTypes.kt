package fan.vault.sdk.models

enum class NftTypes(val text: String) {
    ALBUM("album");

   companion object {
       fun fromText(text: String?) = values().firstOrNull { it.text == text }
   }
}