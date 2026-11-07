package pl.cincior.englishreminder.feature.auth.domain.model

data class AuthToken(val jwtToken: String, val refreshToken: String)