package pl.cincior.englishreminder.core.domain.model.word

data class DeleteWordResult(
    val deleted: Boolean,
    val wordId: String
)