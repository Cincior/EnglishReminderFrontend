package pl.cincior.englishreminder.core.data.util

import com.auth0.android.jwt.DecodeException
import com.auth0.android.jwt.JWT
import kotlinx.coroutines.flow.firstOrNull
import pl.cincior.englishreminder.core.domain.model.UserClaims
import pl.cincior.englishreminder.core.domain.repository.TokenStorage
import javax.inject.Inject

class TokenClaimsService @Inject constructor(
    private val tokenStorage: TokenStorage,
) {
    suspend fun getUserClaims(): UserClaims? {
        try {
            val token = tokenStorage.getAuthTokenSync() ?: tokenStorage.getAuthToken().firstOrNull()
            if (token == null) {
                return null
            }
            val jwt = JWT(token)
            return UserClaims(jwt.getClaim("nickname").asString() ?: "")
        } catch (e: DecodeException) {
            return null
        }
    }
}