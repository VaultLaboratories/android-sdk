package fan.vault.sdk.utils

import android.os.Build.VERSION_CODES.P
import com.google.gson.GsonBuilder
import com.solana.core.HotAccount.Companion.fromJson
import fan.vault.sdk.models.APIError

class APIUtils {
    companion object {
        private val gson by lazy { GsonBuilder().create() }

        fun classifyErrorIfKnown(apiErrorBody: String?): Exception {
            return try {
                val apiError = gson.fromJson(apiErrorBody, APIError::class.java)
                when (apiError.error){
                    "Incorrect OTP or Email" -> IncorrectOTPOrEmailException(apiError.error)
                    else -> Exception(apiErrorBody)
                }
            } catch (_: Exception) {
                Exception(apiErrorBody)
            }
        }
    }
}

class IncorrectOTPOrEmailException(message: String) : Exception(message)