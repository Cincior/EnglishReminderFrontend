package pl.cincior.englishreminder.core.data.firebase

import android.content.Context
import android.util.Log
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import pl.cincior.englishreminder.core.domain.repository.InstallationIdRepository

@HiltWorker
class FcmRegistrationWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParameters: WorkerParameters,
    private val installationIdRepository: InstallationIdRepository
) : CoroutineWorker(appContext, workerParameters) {
    override suspend fun doWork(): Result {
        val token = inputData.getString(TOKEN_KEY) ?: return Result.failure()
        Log.i("Wysyłam z workera doWork", token)

        return if (installationIdRepository.saveInstallationId(token)) {
            Result.success()
        } else if (runAttemptCount < MAX_ATTEMPTS - 1) {
            Result.retry()
        } else {
            installationIdRepository.clearInstallationId()
            Result.success()
        }
    }

    companion object {
        const val TOKEN_KEY = "fcm_token"
        const val UNIQUE_WORK_NAME = "save_fcm_token"
        const val MAX_ATTEMPTS = 3
    }
}
