plugins {
    kotlin("jvm") version "1.4.21"
    id("com.github.johnrengelman.shadow") version "6.1.0"
    application
}

group = "org.example"
version = "1.0-SNAPSHOT"

application {
    mainClassName = "no.nav.rekrutteringsbistand.stillingssokproxy.MainKt"
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(kotlin("stdlib"))
}
