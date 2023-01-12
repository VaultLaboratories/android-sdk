package fan.vault.sdk.utils

import com.google.gson.GsonBuilder
import fan.vault.sdk.models.APIError

class APIUtils {
    companion object {
        private val gson by lazy { GsonBuilder().create() }

        fun classifyErrorIfKnown(apiErrorBody: String?): Exception {
            return try {
                val apiError = gson.fromJson(apiErrorBody, APIError::class.java)
                when (apiError.error){
                    "Incorrect OTP or Email" -> IncorrectOTPOrEmailException(apiError.error)
                    "Could not verify Auth token" -> AuthTokenVerificationFailed(apiError.error)
                    "Don't recognize Auth provider" -> UnrecognisedAuthProvider(apiError.error)
                    else -> Exception(apiErrorBody)
                }
            } catch (_: Exception) {
                Exception(apiErrorBody)
            }
        }
    }
}

class IncorrectOTPOrEmailException(message: String) : Exception(message)

class AuthTokenVerificationFailed(message: String) : Exception(message)

class UnrecognisedAuthProvider(message: String) : Exception(message)