package pl.cincior.englishreminder.core.domain.service

interface TextToSpeechService {
    fun speak(text: String, onError: (String) -> Unit)
    fun stop()
}
