package pl.cincior.englishreminder.feature.words.domain.model

data class UserWord(
    val id: String,
    val word: String,
    val translation: String,
    val insertDateTime: String,
    val category: WordCategory = WordCategory.NOT_LEARNED,
    val memorizeChance: Float
)
