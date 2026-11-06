package pl.cincior.englishreminder.home.domain.model

data class UserWord(
    val id: String,
    val word: String,
    val translation: String,
    val insertDateTime: String,
    val memorizeChance: Float
)