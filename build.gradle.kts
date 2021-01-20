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
    implementation("ch.qos.logback:logback-classic:1.2.3")
    implementation("io.github.cdimascio:dotenv-kotlin:6.2.2")
    implementation("io.javalin:javalin:3.12.0")
    implementation("net.logstash.logback:logstash-logback-encoder:6.3")
    implementation("no.nav.security:token-validation-core:1.3.2")
    implementation("no.nav.security:mock-oauth2-server:0.2.0")
}