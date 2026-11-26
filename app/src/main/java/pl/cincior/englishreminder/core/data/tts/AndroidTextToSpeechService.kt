package pl.cincior.englishreminder.core.data.tts

import android.content.Context
import android.speech.tts.TextToSpeech
import dagger.hilt.android.qualifiers.ApplicationContext
import pl.cincior.englishreminder.core.domain.service.TextToSpeechService
import java.util.Locale
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AndroidTextToSpeechService @Inject constructor(
    @ApplicationContext context: Context
) : TextToSpeechService {
    private var textToSpeech: TextToSpeech? = null
    private var isReady = false
    private var initializationError: String? = null
    private var pendingSpeech: Pair<String, (String) -> Unit>? = null

    init {
        textToSpeech = TextToSpeech(context.applicationContext) { status ->
            if (status != TextToSpeech.SUCCESS) {
                failInitialization("Nie udało się uruchomić syntezatora mowy.")
                return@TextToSpeech
            }

            val engine = textToSpeech ?: return@TextToSpeech
            val languageStatus = engine.setLanguage(Locale.US)
            if (
                languageStatus == TextToSpeech.LANG_MISSING_DATA ||
                languageStatus == TextToSpeech.LANG_NOT_SUPPORTED
            ) {
                failInitialization(
                    "Brak głosu języka angielskiego. Zainstaluj go w ustawieniach syntezatora mowy."
                )
                return@TextToSpeech
            }

            engine.setSpeechRate(0.9f)
            isReady = true
            pendingSpeech?.let { (text, onError) ->
                pendingSpeech = null
                speakNow(text, onError)
            }
        }
    }

    override fun speak(text: String, onError: (String) -> Unit) {
        initializationError?.let {
            onError(it)
            return
        }

        if (!isReady) {
            pendingSpeech = text to onError
            return
        }

        speakNow(text, onError)
    }

    override fun stop() {
        pendingSpeech = null
        textToSpeech?.stop()
    }

    private fun speakNow(text: String, onError: (String) -> Unit) {
        val result = textToSpeech?.speak(
            text,
            TextToSpeech.QUEUE_FLUSH,
            null,
            "word-pronunciation"
        )
        if (result == TextToSpeech.ERROR) {
            onError("Nie udało się odtworzyć wymowy słówka.")
        }
    }

    private fun failInitialization(message: String) {
        initializationError = message
        pendingSpeech?.let { (_, onError) ->
            pendingSpeech = null
            onError(message)
        }
    }
}
