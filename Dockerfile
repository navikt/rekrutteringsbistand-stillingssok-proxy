FROM gcr.io/distroless/java17-debian12:nonroot
ADD build/distributions/rekrutteringsbistand-stillingssok-proxy-1.0-SNAPSHOT.tar /
ENTRYPOINT ["java", "-cp", "/rekrutteringsbistand-stillingssok-proxy/lib/*", "no.nav.rekrutteringsbistand.stillingssokproxy.MainKt"]
EXPOSE 8300
