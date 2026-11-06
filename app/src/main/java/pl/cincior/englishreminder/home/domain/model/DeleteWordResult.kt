package pl.cincior.englishreminder.home.domain.model

data class DeleteWordResult(
    val deleted: Boolean,
    val wordId: String
)