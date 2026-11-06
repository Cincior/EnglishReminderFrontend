package pl.cincior.englishreminder.home.domain.model

data class AddWordResult(
    val created: Boolean,
    val wordId: String?,
    val existingWordId: String?
)