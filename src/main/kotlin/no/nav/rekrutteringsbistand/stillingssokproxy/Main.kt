package no.nav.rekrutteringsbistand.stillingssokproxy

import org.slf4j.Logger
import org.slf4j.LoggerFactory

val Any.log: Logger
    get() = LoggerFactory.getLogger(this::class.java)

fun log(name: String): Logger = LoggerFactory.getLogger(name)

fun main() {
    log("main").info("Starter applikasjon")
}