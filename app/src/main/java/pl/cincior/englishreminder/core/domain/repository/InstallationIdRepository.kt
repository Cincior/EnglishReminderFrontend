package pl.cincior.englishreminder.core.domain.repository

import kotlinx.coroutines.flow.Flow

interface InstallationIdRepository {
    suspend fun saveInstallationId(installationId: String): Boolean
    suspend fun clearInstallationId()
    suspend fun getInstallationId(): Flow<String?>
}