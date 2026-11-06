package pl.cincior.englishreminder.home.domain.model

data class UpdateWordResult(
    val wordId: String,
    val word: String,
    val translation: String
)