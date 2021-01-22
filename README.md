# rekrutteringsbistand-stillingssok-proxy
Proxy for rekrutteringsbistand stillingssøk.

## Start lokalt
For å starte appen lokalt med mock av ElasticSearch må man kjøre main-metoden i Main.kt.<br>
Beskyttelse av endepunkt er da slått av slik at det ikke er nødvendig å sette tokens på requestene.

## Start docker lokalt for testing mot ElasticSearch i DEV
docker build -t stillingssokproxy:latest .<br>
docker run -p 8300:8300 --env NAIS_CLUSTER_NAME=dev-gcp --env ELASTIC_SEARCH_API={adresse} --env ES_USERNAME={brukernavn} --env ES_PASSORD={password} stillingssokproxy:latest<br>

Bytt ut {brukernavn}, {passord} og {adresse} med korrekte verdier for gcp-dev.
Gyldig token må settes på requestene.
