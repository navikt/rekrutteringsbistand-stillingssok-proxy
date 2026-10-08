ARG BASE_IMAGE_DIGEST_PINNED_REF
FROM ${BASE_IMAGE_DIGEST_PINNED_REF}

ENV TZ="Europe/Oslo"

ADD build/distributions/rekrutteringsbistand-stillingssok-proxy-1.0-SNAPSHOT.tar /

ENTRYPOINT ["java", "-cp", "/rekrutteringsbistand-stillingssok-proxy-1.0-SNAPSHOT/lib/*", "no.nav.rekrutteringsbistand.stillingssokproxy.MainKt"]

EXPOSE 8300
