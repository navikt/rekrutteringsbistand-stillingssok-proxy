package no.nav.rekrutteringsbistand.stillingssokproxy

fun main() {
    val javalin = settOppJavalin()
    javalin.start(8300)
}