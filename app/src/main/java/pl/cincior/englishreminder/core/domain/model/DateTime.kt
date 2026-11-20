package pl.cincior.englishreminder.core.domain.model

import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

data class DateTime(
    val date: Long = System.currentTimeMillis(),
    val hour: Int = 12,
    val minute: Int = 0
) {
    fun toISOString(zoneId: ZoneId = ZoneId.of("Europe/Warsaw")): String {
        val effectiveDate = if (date == 0L) System.currentTimeMillis() else date
        return Instant.ofEpochMilli(effectiveDate)
            .atZone(zoneId)
            .toLocalDate()
            .atTime(hour, minute)
            .atZone(zoneId)
            .toInstant()
            .toString()
    }

    fun toFormattedString(zoneId: ZoneId = ZoneId.of("Europe/Warsaw")): String {
        val effectiveDate = if (date == 0L) System.currentTimeMillis() else date
        val localDate = Instant.ofEpochMilli(effectiveDate).atZone(zoneId).toLocalDate()
        val formattedDate = localDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
        val formattedTime = String.format(Locale.getDefault(), "%02d:%02d", hour, minute)
        return "$formattedDate $formattedTime"
    }

    companion object {
        fun formatDateString(dateTimeString: String, zoneId: ZoneId = ZoneId.of("Europe/Warsaw")): String {
            if (dateTimeString.isBlank()) return ""
            return try {
                val instant = Instant.parse(dateTimeString)
                val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")
                instant.atZone(zoneId).format(formatter)
            } catch (_: Exception) {
                dateTimeString
            }
        }
    }
}
