package pl.cincior.englishreminder.feature.home.domain.model

data class TestWordResult(
    val wordId: String,
    val isCorrect: Boolean,
    val testTime: String
)