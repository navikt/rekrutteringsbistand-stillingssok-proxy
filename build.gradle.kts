plugins {
    kotlin("jvm") version "2.3.0"
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

dependencies {
    implementation(platform("io.micrometer:micrometer-bom:1.15.0"))
    testImplementation(platform("org.junit:junit-bom:5.11.4"))

    implementation(kotlin("stdlib"))
    implementation("ch.qos.logback:logback-classic:1.5.16")
    implementation("io.github.cdimascio:dotenv-kotlin:6.5.0")
    implementation("io.javalin:javalin:6.4.0")
    implementation("io.javalin:javalin-micrometer:6.4.0")
    implementation("net.logstash.logback:logstash-logback-encoder:8.0")
    implementation("no.nav.security:token-validation-core:5.0.14")
    implementation("org.opensearch.client:opensearch-rest-high-level-client:2.18.0")
    implementation("io.micrometer:micrometer-core")
    implementation("io.micrometer:micrometer-registry-prometheus")
    implementation("io.prometheus:simpleclient_common:0.16.0")

    testImplementation("com.github.kittinunf.fuel:fuel:2.3.1")
    testImplementation("com.github.kittinunf.fuel:fuel-jackson:2.3.1")
    testImplementation("no.nav.security:mock-oauth2-server:2.1.0")
    testImplementation("org.assertj:assertj-core:3.27.3")
    testImplementation("org.mock-server:mockserver-netty:5.15.0")
    testImplementation("org.junit.jupiter:junit-jupiter-engine")
    testImplementation("org.junit.jupiter:junit-jupiter-api")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}
