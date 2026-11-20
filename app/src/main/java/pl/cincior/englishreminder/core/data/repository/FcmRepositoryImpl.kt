package pl.cincior.englishreminder.core.data.repository

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.apollographql.apollo.ApolloClient
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import pl.cincior.englishreminder.core.di.qualifier.IdentityAuthorizedApolloClient
import pl.cincior.englishreminder.core.domain.repository.InstallationIdRepository
import pl.cincior.englishreminder.identity.graphql.SetInstallationIdMutation
import javax.inject.Inject

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "fcm")

class InstallationIdRepositoryImpl @Inject constructor(
    @param:IdentityAuthorizedApolloClient private val apolloClient: ApolloClient,
    @param:ApplicationContext private val context: Context
) : InstallationIdRepository {
    private companion object {
        val FCM_TOKEN_KEY = stringPreferencesKey("FCM_KEY")
    }

    override suspend fun saveInstallationId(installationId: String): Boolean {
        return try {
            val results = apolloClient.mutation(SetInstallationIdMutation(installationId)).execute()

            if (results.exception == null && results.errors == null) {
                context.dataStore.edit { preferences ->
                    preferences[FCM_TOKEN_KEY] = installationId
                }
                true
            } else {
                false
            }
        } catch (_: Exception) {
            false
        }
    }

    override suspend fun clearInstallationId() {
        context.dataStore.edit { preferences ->
            preferences.remove(FCM_TOKEN_KEY)
        }
    }

    override suspend fun getInstallationId(): Flow<String?> {
        return context.dataStore.data.map { preferences ->
            preferences[FCM_TOKEN_KEY]
        }
    }
}