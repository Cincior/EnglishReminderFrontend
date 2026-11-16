package pl.cincior.englishreminder.core.domain.model.word

data class AddWordResult(
    val created: Boolean,
    val wordId: String?,
    val existingWordId: String?
)