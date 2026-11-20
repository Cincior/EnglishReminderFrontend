package pl.cincior.englishreminder.feature.words.domain.model

data class UpdateWordResult(
    val id: String,
    val word: String,
    val translation: String
)
