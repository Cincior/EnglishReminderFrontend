package pl.cincior.englishreminder.core.domain.model.word

data class UpdateWordResult(
    val wordId: String,
    val word: String,
    val translation: String
)