package pl.cincior.englishreminder.core.domain.model.word

data class TestWordResult(
    val wordId: String,
    val isCorrect: Boolean,
    val testTime: String
)