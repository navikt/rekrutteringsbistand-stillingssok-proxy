plugins {
    kotlin("jvm") version "1.7.20"
    id("com.github.johnrengelman.shadow") version "7.1.2"
    application
}

group = "org.example"
version = "1.0-SNAPSHOT"

application {
    mainClass.set("no.nav.rekrutteringsbistand.stillingssokproxy.MainKt")
}

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(17))
}

tasks.withType<Test> {
    useJUnitPlatform()
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(kotlin("stdlib"))
    implementation("ch.qos.logback:logback-classic:1.4.4")
    implementation("io.github.cdimascio:dotenv-kotlin:6.3.1")
    implementation("io.javalin:javalin:5.1.4")
    implementation("net.logstash.logback:logstash-logback-encoder:7.2")
    implementation("no.nav.security:token-validation-core:2.1.8")
    implementation("org.opensearch.client:opensearch-rest-high-level-client:2.4.0")

    testImplementation("com.github.kittinunf.fuel:fuel:2.3.1")
    testImplementation("com.github.kittinunf.fuel:fuel-jackson:2.3.1")
    testImplementation("no.nav.security:mock-oauth2-server:0.5.5")
    testImplementation("org.assertj:assertj-core:3.23.1")
    testImplementation("org.junit.jupiter:junit-jupiter-engine:5.9.0")
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.9.0")
    testImplementation("org.mock-server:mockserver-netty:5.14.0")
}
