package com.game.tetrixa.devimpact.store

object StoreCooldownFormatter {
    fun formatRemaining(remainingMillis: Long): String {
        val totalMinutes = (remainingMillis + MILLIS_PER_MINUTE - 1L) / MILLIS_PER_MINUTE
        val hours = totalMinutes / MINUTES_PER_HOUR
        val minutes = totalMinutes % MINUTES_PER_HOUR

        return if (hours > 0L) {
            "${hours}h ${minutes}m"
        } else {
            "${minutes.coerceAtLeast(1L)}m"
        }
    }

    private const val MILLIS_PER_MINUTE = 60L * 1000L
    private const val MINUTES_PER_HOUR = 60L
}
