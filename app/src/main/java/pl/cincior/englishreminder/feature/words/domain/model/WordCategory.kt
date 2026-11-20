package pl.cincior.englishreminder.feature.words.domain.model

enum class WordCategory(val displayName: String) {
    LEARN_NOW("Ucz się teraz (Learn Now)"),
    LEARN_LATER("Ucz się później (Learn Later)"),
    LEARNED("Nauczone (Learned)"),
    NOT_LEARNED("Nienauczone (Not Learned)")
}
