# rekrutteringsbistand-stillingssok-proxy
Proxy for rekrutteringsbistand stillingssøk.

## Start lokalt
For å starte appen lokalt med mock av ElasticSearch er det bare å kjøre main-metoden i Main.kt.<br>
Beskyttelse av endepunkt er da slått av slik at det ikke er nødvendig å sette tokens på requestene.

## Start docker lokalt for testing mot ElasticSearch i DEV
docker build -t stillingssokproxy:latest .<br>
docker run -p 8300:8300 --env-file .env --env ES_USERNAME={brukernavn} --env ES_PASSORD={password} stillingssokproxy:latest<br>

Bytt ut {brukernavn} og {passord} med korrekte verdier for kjøremiljøet gcp-dev.
Gyldig token må settes på requestene.

## Kjør tester i Intellij
Du må gå til settings, velg build/execution/deployment 
-> build tools 
-> gradle 
-> Run tests using
-> Intellij