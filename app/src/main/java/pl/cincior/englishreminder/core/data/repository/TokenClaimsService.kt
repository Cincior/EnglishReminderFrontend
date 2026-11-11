package pl.cincior.englishreminder.core.data.repository

import com.auth0.android.jwt.DecodeException
import com.auth0.android.jwt.JWT
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.runBlocking
import pl.cincior.englishreminder.core.domain.model.UserClaims
import pl.cincior.englishreminder.core.domain.repository.TokenStorage
import javax.inject.Inject

class TokenClaimsService @Inject constructor(
    private val tokenStorage: TokenStorage,
) {
    fun getUserClaims(): UserClaims {
        val userClaims = UserClaims()
        try {
            val token = runBlocking {
                tokenStorage.getAuthToken().firstOrNull()
            }
            if (token == null) {
                return userClaims
            }
            val jwt = JWT(token)
            return UserClaims(jwt.getClaim("nickname").asString() ?: "")
        } catch (e: DecodeException) {
            return userClaims
        }
    }
}