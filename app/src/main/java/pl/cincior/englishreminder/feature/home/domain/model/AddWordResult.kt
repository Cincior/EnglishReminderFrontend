package pl.cincior.englishreminder.feature.home.domain.model

data class AddWordResult(
    val created: Boolean,
    val wordId: String?,
    val existingWordId: String?
)