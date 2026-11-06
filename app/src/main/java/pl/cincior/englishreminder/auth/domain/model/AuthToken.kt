package pl.cincior.englishreminder.auth.domain.model

data class AuthToken(val jwtToken: String, val refreshToken: String)