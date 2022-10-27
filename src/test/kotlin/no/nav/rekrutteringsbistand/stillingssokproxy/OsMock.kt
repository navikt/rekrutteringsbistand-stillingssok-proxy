package no.nav.rekrutteringsbistand.stillingssokproxy

import org.mockserver.integration.ClientAndServer
import org.mockserver.model.HttpRequest.request
import org.mockserver.model.HttpResponse.response
import org.mockserver.model.MediaType

object OsMock {

    var serverHasStarted = false

    fun startOsMock() {
        if (serverHasStarted) return
        ClientAndServer.startClientAndServer(9000)
            .`when`(
                request()
                    .withMethod("GET")
                    .withPath(".*")
            )
            .respond(
                response()
                    .withStatusCode(200)
                    .withContentType(MediaType.APPLICATION_JSON)
                    .withBody(jsonResultat)
            )
        serverHasStarted = true
    }

    val jsonResultat = """
    {
      "took": 5,
      "timed_out": false,
      "_shards": {
        "total": 3,
        "successful": 3,
        "skipped": 0,
        "failed": 0
      },
      "hits": {
        "total": {
          "value": 1,
          "relation": "eq"
        },
        "max_score": 9.65912,
        "hits": [
          {
            "_index": "stilling_6",
            "_type": "_doc",
            "_id": "2c40db73-2d2c-48fa-92ec-402e5ad6e32d",
            "_score": 9.65912,
            "_source": {
              "stilling": {
                "title": "Construction Lead Foreship and LQ",
                "uuid": "2c40db73-2d2c-48fa-92ec-402e5ad6e32d",
                "status": "INACTIVE",
                "privacy": "SHOW_ALL",
                "published": "2020-11-05T07:05:04.400094",
                "expires": "2020-11-10T00:00:00",
                "created": "2020-11-05T07:10:49.733411",
                "updated": "2020-11-10T01:33:01.420867",
                "employer": {
                  "name": "IKM CONSULTANTS MANAGEMENT AS",
                  "publicName": "IKM CONSULTANTS MANAGEMENT AS",
                  "orgnr": "825523472",
                  "parentOrgnr": "925506974",
                  "orgform": "BEDR"
                },
                "categories": [
                  {
                    "styrkCode": "1323.01",
                    "name": "Leder (bygg og anlegg)"
                  }
                ],
                "source": "FINN",
                "medium": "FINN",
                "businessName": "IKM Consultants AS",
                "locations": [
                  {
                    "address": null,
                    "postalCode": "4051",
                    "county": "ROGALAND",
                    "municipal": "SOLA",
                    "latitue": null,
                    "longitude": null
                  }
                ],
                "reference": "197208702",
                "administration": {
                  "status": "RECEIVED",
                  "remarks": [],
                  "comments": "",
                  "reportee": "",
                  "navIdent": ""
                },
                "properties": {
                  "sourceurl": "https://www.finn.no/197208702",
                  "classification_styrk08_score": 0.31071928,
                  "jobtitle": "Construction Lead Foreship and LQ",
                  "author": "IKM Consultants AS",
                  "engagementtype": "Vikariat",
                  "classification_input_source": "jobtitle",
                  "employerdescription": "<p>IKM Consultants AS is a company within the IKM Group. The company is one of Norway&#39;s largest Norwegian owned consultant companies, which offers multi-discipline engineers both nationally and internationally. Our consultants have a high level of education and experience. They participate in all project phases; from initial studies and planning activities to project execution and close-out. Our company is growing continuously and require competent people to supplement our existing staff.</p>\n",
                  "sector": "Privat",
                  "sourceupdated": "2020-11-04T10:00:00.000Z",
                  "applicationurl": "https://secure.webtemp.no/WebtempCVPortal/startsession.aspx?k=65&page=default&id_utleie=3013298",
                  "applicationdue": "10.11.2020",
                  "extent": "Heltid",
                  "occupation": "Ingeniør",
                  "logomain": "https://images.finncdn.no/mmo/logo/object/2064108118/ikm1.png",
                  "applicationlabel": 411497,
                  "externalref": 20041149701,
                  "logolisting": "https://images.finncdn.no/mmo/logo/result/2064108118/iad_6357597606626452165ikm1_medium.png",
                  "employerhomepage": "http://www.ikmconsultants.no",
                  "starttime": "01.12.2020",
                  "searchtags": [
                    {
                      "label": "Byggeleder",
                      "score": 0.31058732
                    },
                    {
                      "label": "Rutesjef varetransport",
                      "score": 0.06618702
                    },
                    {
                      "label": "Construction Manager - Surface/Insulation",
                      "score": 0.052760284
                    },
                    {
                      "label": "Lead Preconstruction Manager",
                      "score": 0.0036730855
                    },
                    {
                      "label": "Byggeleder betongarbeid",
                      "score": 0.0026657667
                    }
                  ],
                  "employer": "IKM Consultants AS",
                  "industry": "Olje og gass",
                  "adtext": "<h2>Responsibilities</h2>\n<ul><li>Area lead for Foreship and LQ construction activities, reporting to Construction Manager</li><li>Responsible for follow up of all construction activities at Foreship and LQ</li><li>Communicate and coordinate activities related to Foreship and LQ construction with yard</li><li>Coordinate activities and follow-up of construction and MC of Foreship and LQ</li><li>Follow project to Stord and ensure continuity for Foreship and LQ after moving from Singapore to Stord</li></ul>\n<p>Typical tasks:</p>\n<ul><li>Coordinate Construction follow-up team at Foreship and LQ</li><li>Monitor construction quality and progress</li><li>Report progress</li><li>Follow-up of HSE standards and implementation of this on yard</li><li>Participate in HSE inspections</li><li>Communicate with Integration Team to ensure smooth transition to Integration site</li></ul>\n<h2>Required expertise</h2>\n<p>Competence:</p>\n<ul><li>Relevant theoretical background on bachelor level or higher</li><li>Minimum 10 year of site experience from shipbuilding and/or oil &amp; gas floater projects</li><li>Extensive knowledge of Class, NORSOK and NCS regulations</li></ul>\n<h2>Personal skills</h2>\n<ul><li>High focus on HSE and Values</li><li>Structured, systematic and target driven</li><li>Multidiscipline understanding and ability to work across all areas in the project</li><li>Ability to handle responsibility and periodically high work load</li><li>Hands on attitude</li></ul>\n<p>Language requirements: English<br />\nWork location: Singapore - approx. until September 30th, 2021, Stord afterwards - until September 30th, 2022<br />\nDuration: 01.12.20-30.09.22</p>\n",
                  "location": "Singapore"
                }
              },
              "stillingsinfo": null
            }
          }
        ]
      }
    }
""".trimIndent()
}