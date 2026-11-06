package pl.cincior.englishreminder.feature.auth.domain.model

data class RegisterPayload(
    val registered: Boolean,
    val verificationRequired: Boolean
)
