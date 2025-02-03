plugins {
    kotlin("jvm") version "2.1.0"
    application
}

group = "org.example"
version = "1.0-SNAPSHOT"

application {
    mainClass.set("no.nav.rekrutteringsbistand.stillingssokproxy.MainKt")
}

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(21))
}

tasks.withType<Test> {
    useJUnitPlatform()
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(kotlin("stdlib"))
    implementation("ch.qos.logback:logback-classic:1.5.16")
    implementation("io.github.cdimascio:dotenv-kotlin:6.5.0")
    implementation("io.javalin:javalin:6.4.0")
    implementation("net.logstash.logback:logstash-logback-encoder:8.0")
    implementation("no.nav.security:token-validation-core:5.0.14")
    implementation("org.opensearch.client:opensearch-rest-high-level-client:2.18.0")

    testImplementation("com.github.kittinunf.fuel:fuel:2.3.1")
    testImplementation("com.github.kittinunf.fuel:fuel-jackson:2.3.1")
    testImplementation("no.nav.security:mock-oauth2-server:2.1.0")
    testImplementation("org.assertj:assertj-core:3.27.3")
    testImplementation("org.junit.jupiter:junit-jupiter-engine:5.11.4")
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.11.4")
    testImplementation("org.mock-server:mockserver-netty:5.15.0")
}
