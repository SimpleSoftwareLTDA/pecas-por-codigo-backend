package org.pecasonline.features.items.swagger

class ItemExample {
    companion object {
        const val GET_ALL_ITEMS = """
            {
              "content": [
                {
                  "id": 232,
                  "manufacturer": "Distribuidor",
                  "code": "52046262",
                  "priceInCents": null,
                  "description": "FILTRO",
                  "updateDate": "2024-10-27T03:00:00.000+00:00",
                  "hash": "d3b3d22af71cb9992b7001f0448301e6"
                },
                {
                  "id": 233,
                  "manufacturer": "Distribuidor",
                  "code": "12581701",
                  "priceInCents": null,
                  "description": "POLIA TENSORA S10 2012/",
                  "updateDate": "2024-10-27T03:00:00.000+00:00",
                  "hash": "14ea96e6b5528a5d1fffe55c7bc6f225"
                }
              ],
              "pageable": {
                "pageNumber": 0,
                "pageSize": 2,
                "sort": {
                  "empty": true,
                  "unsorted": true,
                  "sorted": false
                },
                "offset": 0,
                "unpaged": false,
                "paged": true
              },
              "last": false,
              "totalElements": 12991,
              "totalPages": 6496,
              "first": true,
              "size": 2,
              "number": 0,
              "sort": {
                "empty": true,
                "unsorted": true,
                "sorted": false
              },
              "numberOfElements": 2,
              "empty": false
            }
        """

        const val GET_ITEM_BY_ID = """
            {
              "id": 244,
              "manufacturer": "Suzuki",
              "code": "93263176",
              "priceInCents": 106004,
              "description": "PARA LAMA PARA CARRO",
              "updateDate": "2024-10-27T03:00:00.000+00:00",
              "hash": "3ecbad378aee74d226fc3181dd1efe55"
            }
        """

        const val GET_ITEM_BY_DESCRIPTION = """
            {
              "content": [
                {
                  "id": 441,
                  "manufacturer": "General Motors",
                  "code": "94754484",
                  "priceInCents": 11555,
                  "description": "BUCHA",
                  "updateDate": "2024-10-27T03:00:00.000+00:00",
                  "hash": "5217bedfef7af4ec33b52a03085b74ca"
                },
                {
                  "id": 483,
                  "manufacturer": "General Motors",
                  "code": "93294976",
                  "priceInCents": 4783,
                  "description": "BUCHA",
                  "updateDate": "2024-10-27T03:00:00.000+00:00",
                  "hash": "7ff89af2540494a4a1cf339e6a162da1"
                }
              ],
              "pageable": {
                "pageNumber": 0,
                "pageSize": 2,
                "sort": {
                  "empty": true,
                  "unsorted": true,
                  "sorted": false
                },
                "offset": 0,
                "unpaged": false,
                "paged": true
              },
              "last": false,
              "totalElements": 210,
              "totalPages": 105,
              "first": true,
              "size": 2,
              "number": 0,
              "sort": {
                "empty": true,
                "unsorted": true,
                "sorted": false
              },
              "numberOfElements": 2,
              "empty": false
            }
        """

        const val GET_ITEM_BY_CODE = """
            {
              "content": [
                {
                  "id": 430,
                  "manufacturer": "General Motors",
                  "code": "94754484",
                  "priceInCents": 11555,
                  "description": "ISOLADOR DE BORRACHA",
                  "updateDate": "2024-10-27T03:00:00.000+00:00",
                  "hash": "6c0dfcabe9f257766b98ab12ebe4ddc8"
                },
                {
                  "id": 431,
                  "manufacturer": "Distribuidor",
                  "code": "94754484",
                  "priceInCents": 10826,
                  "description": "\"Borracha Estabilizador Onix 2013 / 2 Peça Gm 9475",
                  "updateDate": "2024-10-27T03:00:00.000+00:00",
                  "hash": "ca2068bf97f98362cca9b0b1b9060c10"
                }
              ],
              "pageable": {
                "pageNumber": 0,
                "pageSize": 2,
                "sort": {
                  "empty": true,
                  "unsorted": true,
                  "sorted": false
                },
                "offset": 0,
                "unpaged": false,
                "paged": true
              },
              "last": false,
              "totalElements": 5,
              "totalPages": 3,
              "first": true,
              "size": 2,
              "number": 0,
              "sort": {
                "empty": true,
                "unsorted": true,
                "sorted": false
              },
              "numberOfElements": 2,
              "empty": false
            }
        """
    }
}