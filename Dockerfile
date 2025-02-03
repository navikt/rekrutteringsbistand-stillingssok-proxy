FROM gcr.io/distroless/java21-debian12:nonroot
ADD build/distributions/rekrutteringsbistand-stillingssok-proxy-1.0-SNAPSHOT.tar /
ENTRYPOINT ["java", "-cp", "/rekrutteringsbistand-stillingssok-proxy-1.0-SNAPSHOT/lib/*", "no.nav.rekrutteringsbistand.stillingssokproxy.MainKt"]
EXPOSE 8300
