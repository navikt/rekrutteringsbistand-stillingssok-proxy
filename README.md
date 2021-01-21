# rekrutteringsbistand-stillingssok-proxy
Proxy for rekrutteringsbistand stillingssøk.

## Start lokalt
For å starte appen lokalt er det bare å kjøre main-metoden i Main.kt.<br>
Beskyttelse av endepunkt er da slått av slik at det ikke er nødvendig å sette tokens på requestene.

## Start docker lokalt
docker run -p 8300:8300 --env-file .env stillingssokproxy:latest<br>
Heller ikke med docker lokalt vil endepunktene være beskyttet.

## Kjør tester i Intellij
Du må gå til settings, velg build/execution/deployment 
-> build tools 
-> gradle 
-> Run tests using
-> Intellij