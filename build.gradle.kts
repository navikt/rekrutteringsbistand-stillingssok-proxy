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
    implementation("org.elasticsearch.client:elasticsearch-rest-high-level-client:7.10.1")

    testImplementation("com.github.kittinunf.fuel:fuel:2.3.0")
    testImplementation("com.github.kittinunf.fuel:fuel-jackson:2.3.0")
    testImplementation("org.assertj:assertj-core:3.18.1")
    testImplementation("org.junit.jupiter:junit-jupiter-engine:5.0.0")
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.7.0")
    testImplementation ("com.github.tomakehurst:wiremock-jre8:2.27.2")
}