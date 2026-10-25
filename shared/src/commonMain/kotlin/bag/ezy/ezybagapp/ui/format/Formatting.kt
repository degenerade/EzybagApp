package bag.ezy.ezybagapp.ui.format

import kotlinx.datetime.LocalDate

private val days = listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")
private val months = listOf("Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec")

fun formatDisplayDate(iso: String): String {
    val date = runCatching { LocalDate.parse(iso) }.getOrNull() ?: return iso
    return "${days[date.dayOfWeek.ordinal]} ${date.day} ${months[date.month.ordinal]}"
}

fun formatDisplayTime(hhmm: String): String {
    val parts = hhmm.split(":").mapNotNull { it.toIntOrNull() }
    if (parts.size != 2) return hhmm
    val (h, m) = parts
    val hour12 = if (h % 12 == 0) 12 else h % 12
    return "$hour12:${m.toString().padStart(2, '0')} ${if (h < 12) "AM" else "PM"}"
}

fun formatPrice(amount: Double): String = "£${amount.toInt()}"