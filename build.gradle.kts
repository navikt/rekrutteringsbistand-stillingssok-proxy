plugins {
    kotlin("jvm") version embeddedKotlinVersion
    id("com.github.johnrengelman.shadow") version "7.1.2"
    application
}

group = "org.example"
version = "1.0-SNAPSHOT"

application {
    mainClass.set("no.nav.rekrutteringsbistand.stillingssokproxy.MainKt")
}

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(16))
}

tasks.withType<Test> {
    useJUnitPlatform()
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(kotlin("stdlib"))
    implementation("ch.qos.logback:logback-classic:1.2.7")
    implementation("io.github.cdimascio:dotenv-kotlin:6.2.2")
    implementation("io.javalin:javalin:4.1.1")
    implementation("net.logstash.logback:logstash-logback-encoder:7.0")
    implementation("no.nav.security:token-validation-core:1.3.9")
    implementation("org.elasticsearch.client:elasticsearch-rest-high-level-client:7.15.0")

    testImplementation("com.github.kittinunf.fuel:fuel:2.3.1")
    testImplementation("com.github.kittinunf.fuel:fuel-jackson:2.3.1")
    testImplementation("no.nav.security:mock-oauth2-server:0.3.6")
    testImplementation("org.assertj:assertj-core:3.21.0")
    testImplementation("org.junit.jupiter:junit-jupiter-engine:5.8.1")
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.8.1")
    testImplementation("org.mock-server:mockserver-netty:5.11.2")
}
