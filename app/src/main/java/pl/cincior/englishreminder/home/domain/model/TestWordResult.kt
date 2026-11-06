package pl.cincior.englishreminder.home.domain.model

data class TestWordResult(
    val wordId: String,
    val isCorrect: Boolean,
    val testTime: String
)