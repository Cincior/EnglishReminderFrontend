package pl.cincior.englishreminder.feature.words.domain.model

data class AddWordResult(
    val created: Boolean,
    val wordId: String?,
    val existingWordId: String?
)
