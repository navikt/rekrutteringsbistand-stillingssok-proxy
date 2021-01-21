package no.nav.rekrutteringsbistand.stillingssokproxy

fun sok(jsonbody: String, params: Map<String, List<String>>) : String {
    val queryMap = params.entries.map { (key, value) -> return@map Pair(key, value[0]) }.toMap()
    return "svar"
}