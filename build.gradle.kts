plugins {
    kotlin("jvm") version "2.4.20"
    application
}

group = "org.example"
version = "1.0-SNAPSHOT"

application {
    mainClass.set("no.nav.rekrutteringsbistand.stillingssokproxy.MainKt")
}

kotlin {
    jvmToolchain(25)
}

tasks.withType<Test> {
    useJUnitPlatform()
}

repositories {
    mavenCentral()
}

val javalinVersion = "7.2.3"

dependencies {
    implementation(platform("io.micrometer:micrometer-bom:1.17.1"))
    testImplementation(platform("org.junit:junit-bom:6.1.3"))

    implementation(kotlin("stdlib"))
    implementation("ch.qos.logback:logback-classic:1.6.5")
    implementation("io.github.cdimascio:dotenv-kotlin:6.5.1")
    implementation("io.javalin:javalin:$javalinVersion")
    implementation("io.javalin:javalin-micrometer:$javalinVersion")
    implementation("net.logstash.logback:logstash-logback-encoder:9.0")
    implementation("no.nav.security:token-validation-core:5.0.30")
    implementation("org.opensearch.client:opensearch-java:3.10.0")
    implementation("io.micrometer:micrometer-core")
    implementation("io.micrometer:micrometer-registry-prometheus")
    implementation("io.prometheus:simpleclient_common:0.16.0")

    testImplementation("com.github.kittinunf.fuel:fuel:2.3.1")
    testImplementation("com.github.kittinunf.fuel:fuel-jackson:2.3.1")
    testImplementation("no.nav.security:mock-oauth2-server:2.3.0")
    testImplementation("org.assertj:assertj-core:3.27.7")
    testImplementation("org.mock-server:mockserver-netty:8.0.0")
    testImplementation("org.junit.jupiter:junit-jupiter-engine")
    testImplementation("org.junit.jupiter:junit-jupiter-api")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}
