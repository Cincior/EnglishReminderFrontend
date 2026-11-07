package pl.cincior.englishreminder.feature.home.domain.model

data class UserWord(
    val id: String,
    val word: String,
    val translation: String,
    val insertDateTime: String,
    val memorizeChance: Float
)