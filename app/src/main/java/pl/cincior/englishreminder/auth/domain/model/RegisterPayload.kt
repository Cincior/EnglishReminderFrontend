package pl.cincior.englishreminder.auth.domain.model

data class RegisterPayload(
    val registered: Boolean,
    val verificationRequired: Boolean
)
