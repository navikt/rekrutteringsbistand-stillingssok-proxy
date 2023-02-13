# rekrutteringsbistand-stillingssok-proxy
Proxy for rekrutteringsbistand stillingssøk.

## Start app lokalt
For å starte appen lokalt med mock av OpenSearch må man kjøre main-metoden i LokalApplikasjon.<br>
Beskyttelse av endepunkt er da slått av slik at det ikke er nødvendig å sette tokens på requestene.

## Spørre manuelt mot Opensearch
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
  av [team Toi i produktområde Arbeidsgiver](https://teamkatalog.nav.no/team/76f378c5-eb35-42db-9f4d-0e8197be0131).
* Slack-kanaler:
    * [#arbeidsgiver-toi-dev](https://nav-it.slack.com/archives/C02HTU8DBSR)
    * [#rekrutteringsbistand-værsågod](https://nav-it.slack.com/archives/C02HWV01P54)

## For folk utenfor Nav

IT-avdelingen
i [Arbeids- og velferdsdirektoratet](https://www.nav.no/no/NAV+og+samfunn/Kontakt+NAV/Relatert+informasjon/arbeids-og-velferdsdirektoratet-kontorinformasjon)
