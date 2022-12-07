package fan.vault.sdk.utils

class APIUtils {
    companion object {
        fun classifyErrorIfKnown(errorMessage: String): Exception? {
            return when (errorMessage) {
                "Incorrect OTP or Email" -> IncorrectOTPOrEmail(errorMessage)
                else -> null
            }
        }
    }
}

class IncorrectOTPOrEmail(message: String) : Exception(message)