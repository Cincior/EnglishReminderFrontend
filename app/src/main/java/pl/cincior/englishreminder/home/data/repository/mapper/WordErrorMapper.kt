package pl.cincior.englishreminder.home.data.repository.mapper

import com.apollographql.apollo.exception.ApolloException
import com.apollographql.apollo.exception.ApolloHttpException
import com.apollographql.apollo.exception.ApolloNetworkException
import pl.cincior.englishreminder.core.domain.WordError

fun String?.toWordError(): WordError = when (this) {
    "NO_WORDS_FOUND" -> WordError.Data.NO_WORDS_FOUND
    "WORD_NOT_FOUND" -> WordError.Data.WORD_NOT_FOUND
    "DUPLICATED_WORD" -> WordError.Data.DUPLICATED_WORD
    "FAILED_TO_ADD_WORD" -> WordError.Data.FAILED_TO_ADD_WORD
    "INVALID_IDENTIFIER" -> WordError.Data.INVALID_IDENTIFIER
    "INVALID_WORD_DATA" -> WordError.Data.INVALID_WORD_DATA
    "CANNOT_CALCULATE_PREDICTION" -> WordError.Data.CANNOT_CALCULATE_PREDICTION
    "PREDICTION_SERVICE_ERROR" -> WordError.Data.PREDICTION_SERVICE_ERROR
    "CATEGORY_NOT_FOUND" -> WordError.Data.CATEGORY_NOT_FOUND
    "WRONG_TIME_FORMAT" -> WordError.Data.WRONG_TIME_FORMAT
    "WRONG_TIME" -> WordError.Data.WRONG_TIME
    "FAILED_TO_SCHEDULE_NOTIFICATION" -> WordError.Data.FAILED_TO_SCHEDULE_NOTIFICATION
    else -> WordError.Data.UNKNOWN_ERROR
}

fun ApolloException.toWordException(): WordError.Network {
    return when (this) {
        is ApolloNetworkException -> WordError.Network.NO_CONNECTION
        is ApolloHttpException -> if (statusCode >= 500) WordError.Network.SERVER_ERROR
        else WordError.Network.UNKNOWN
        else                      -> WordError.Network.UNKNOWN
    }
}