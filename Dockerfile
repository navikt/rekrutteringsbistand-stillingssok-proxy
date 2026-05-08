FROM europe-north1-docker.pkg.dev/cgr-nav/pull-through/nav.no/jre:openjdk-25
ENV TZ="Europe/Oslo"
ADD build/distributions/rekrutteringsbistand-stillingssok-proxy-1.0-SNAPSHOT.tar /
ENTRYPOINT ["java", "-cp", "/rekrutteringsbistand-stillingssok-proxy-1.0-SNAPSHOT/lib/*", "no.nav.rekrutteringsbistand.stillingssokproxy.MainKt"]
EXPOSE 8300
