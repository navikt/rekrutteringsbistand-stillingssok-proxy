FROM ghcr.io/navikt/baseimages/temurin:17
COPY ./build/libs/rekrutteringsbistand-stillingssok-proxy-1.0-SNAPSHOT-all.jar app.jar

EXPOSE 8300
