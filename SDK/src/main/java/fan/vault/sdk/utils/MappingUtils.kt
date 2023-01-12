package fan.vault.sdk.utils

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import fan.vault.sdk.models.*

class MappingUtils {
    companion object {
        fun mapFileExt(metadata: JsonMetadataExt): List<Any> {
            var newFileList: List<Any> = emptyList()
            metadata.files.map { anyFile ->
                val mime = (anyFile as java.util.LinkedHashMap<*, *>)["mime"].toString()
                newFileList = if (mime.contains("audio")) {
                    newFileList.plus(
                        jacksonObjectMapper().convertValue(
                            anyFile,
                            JsonMetadataAudioFileExt::class.java
                        ).apply {
                            this.encryption?.let { this.encryption = mapEncryptionData(it) }
                        }
                    )
                } else {
                    newFileList.plus(
                        jacksonObjectMapper().convertValue(
                            anyFile,
                            JsonMetadataFileExt::class.java
                        ).apply {
                            this.encryption?.let { this.encryption = mapEncryptionData(it) }
                        }
                    )
                }
            }
            return newFileList
        }

        private fun mapEncryptionData(encryption: Encryption): Encryption {
            return if (encryption.provider == EncryptionProvider.LIT_PROTOCOL) {
                Encryption(
                    EncryptionProvider.LIT_PROTOCOL,
                    jacksonObjectMapper().convertValue(
                        encryption.providerData,
                        LitProtocolData::class.java
                    )
                )
            } else {
                encryption
            }
        }
    }
}