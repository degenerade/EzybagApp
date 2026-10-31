package bag.ezy.ezybagapp.ui.search

fun matchesSearch(query: String, vararg fields: String?): Boolean {
    val words = query.trim().lowercase().split(Regex("\\s+")).filter { it.isNotEmpty() }
    if (words.isEmpty()) return true

    val text = fields.filterNotNull().joinToString(" ").lowercase()
    val textNoSpaces = text.replace(" ", "")

    return words.all { word -> word in text || word in textNoSpaces }
}