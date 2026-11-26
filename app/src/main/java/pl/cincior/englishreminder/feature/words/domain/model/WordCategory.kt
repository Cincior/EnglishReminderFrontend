package pl.cincior.englishreminder.feature.words.domain.model

enum class WordCategory(val displayName: String) {
    LEARN_NOW("Ucz się teraz"),
    LEARN_LATER("Ucz się później"),
    LEARNED("Nauczone"),
    NOT_LEARNED("Nienauczone")
}
