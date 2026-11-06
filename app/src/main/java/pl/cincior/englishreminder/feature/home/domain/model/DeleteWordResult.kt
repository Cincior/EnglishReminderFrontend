package pl.cincior.englishreminder.feature.home.domain.model

data class DeleteWordResult(
    val deleted: Boolean,
    val wordId: String
)