package fan.vault.sdk.utils

class APIUtils {
    companion object {
        fun throwError(errorMessage: String) {
            when (errorMessage) {
                "Incorrect OTP or Email" -> throw IncorrectOTPOrEmail()
                else -> throw Exception()
            }
        }
    }
}

class IncorrectOTPOrEmail : Exception()