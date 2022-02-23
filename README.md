# rekrutteringsbistand-stillingssok-proxy
Proxy for rekrutteringsbistand stillingssøk.

## Start app lokalt
For å starte appen lokalt med mock av ElasticSearch må man kjøre main-metoden i LokalApplikasjon.<br>
Beskyttelse av endepunkt er da slått av slik at det ikke er nødvendig å sette tokens på requestene.

## Spørre manuelt mot Elasticsearch
Gjøres med å sende en HTTP GET request, som kan gjøres på mange forskjellige måter. I dette eksemplet vises syntaksen til IntelliJ sitt innebygde verktøy "HTTP Client":
```
GET https://elastic-arbeidsgiver-rekrutteringsbistand-stilling-nav-prod.aivencloud.com:26482/stilling/_search
Authorization: Basic arbeidsgiver-r <passord>
Content-Type: application/json

{
  "query": {
    "query_string": {
      "query": "<søkeord, kan være hva som helst, f.eks. stillingens annonsenummer>"
    }
  }
}
```

URL, brukernavn og passord hentes fra en Kubernetes-pod slik:
1. I Naisdevice-appen, koble deg til `aiven-prod`
2. Finn navnet på en kjørende Kubernetes-pod ved å kjøre f.eks. `kubectl get pods -n arbeidsgiver | grep rekrutteringsbistand-stillingssok-proxy`
3. Logg inn i pod-en ved å kjøre `kubectl exec -it <podnavn> -n arbeidsgiver -- /bin/sh`
4. Inne i pod-en, vis miljøvariabler med URL, brukernavn og passord ved å kjøre f.eks. `env | grep -i elastic` 


# Henvendelser

## For Nav-ansatte

* Dette Git-repositoriet eies
  av [Team tiltak og inkludering (TOI) i Produktområde arbeidsgiver](https://teamkatalog.nais.adeo.no/team/0150fd7c-df30-43ee-944e-b152d74c64d6)
  .
* Slack-kanaler:
  * [#arbeidsgiver-toi-dev](https://nav-it.slack.com/archives/C02HTU8DBSR)
  * [#arbeidsgiver-utvikling](https://nav-it.slack.com/archives/CD4MES6BB)

## For folk utenfor Nav

* Opprett gjerne en issue i Github for alle typer spørsmål
* IT-utviklerne i Github-teamet https://github.com/orgs/navikt/teams/toi
* IT-avdelingen
  i [Arbeids- og velferdsdirektoratet](https://www.nav.no/no/NAV+og+samfunn/Kontakt+NAV/Relatert+informasjon/arbeids-og-velferdsdirektoratet-kontorinformasjon)
