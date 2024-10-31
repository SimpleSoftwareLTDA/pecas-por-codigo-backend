package org.pecasonline.features.items.swagger

class ItemExample {
    companion object {
        const val GET_ALL_ITEMS = """
           {
              "content": [
                {
                  "id": 232,
                  "fabricante": "Distribuidor",
                  "codigo": "52046262",
                  "precoEmCentavos": null,
                  "descricao": "FILTRO",
                  "dataDeAtualizacao": "2024-10-27T03:00:00.000+00:00",
                  "hash": "d3b3d22af71cb9992b7001f0448301e6"
                },
                {
                  "id": 233,
                  "fabricante": "Distribuidor",
                  "codigo": "12581701",
                  "precoEmCentavos": null,
                  "descricao": "POLIA TENSORA S10 2012/",
                  "dataDeAtualizacao": "2024-10-27T03:00:00.000+00:00",
                  "hash": "14ea96e6b5528a5d1fffe55c7bc6f225"
                }
              ],
              "pageable": {
                "pageNumber": 0,
                "pageSize": 2,
                "sort": {
                  "empty": true,
                  "sorted": false,
                  "unsorted": true
                },
                "offset": 0,
                "paged": true,
                "unpaged": false
              },
              "last": false,
              "totalPages": 6496,
              "totalElements": 12991,
              "first": true,
              "size": 2,
              "number": 0,
              "sort": {
                "empty": true,
                "sorted": false,
                "unsorted": true
              },
              "numberOfElements": 2,
              "empty": false
            }
        """

        const val GET_ITEM_BY_ID = """
            {
              "id": 344,
              "fabricante": "General Motors",
              "codigo": "52126000",
              "precoEmCentavos": 75754,
              "descricao": "CAIXA-FUSIVEIS",
              "dataDeAtualizacao": "2024-10-27T03:00:00.000+00:00",
              "hash": "74174ba88ebb073e57f16e72d7d61f7a"
            }
        """

        const val GET_ITEM_BY_DESCRIPTION = """
           {
              "content": [
                {
                  "id": 441,
                  "fabricante": "General Motors",
                  "codigo": "94754484",
                  "precoEmCentavos": 11555,
                  "descricao": "BUCHA",
                  "dataDeAtualizacao": "2024-10-27T03:00:00.000+00:00",
                  "hash": "5217bedfef7af4ec33b52a03085b74ca"
                },
                {
                  "id": 483,
                  "fabricante": "General Motors",
                  "codigo": "93294976",
                  "precoEmCentavos": 4783,
                  "descricao": "BUCHA",
                  "dataDeAtualizacao": "2024-10-27T03:00:00.000+00:00",
                  "hash": "7ff89af2540494a4a1cf339e6a162da1"
                }
              ],
              "pageable": {
                "pageNumber": 0,
                "pageSize": 2,
                "sort": {
                  "empty": true,
                  "sorted": false,
                  "unsorted": true
                },
                "offset": 0,
                "paged": true,
                "unpaged": false
              },
              "last": false,
              "totalPages": 105,
              "totalElements": 210,
              "first": true,
              "size": 2,
              "number": 0,
              "sort": {
                "empty": true,
                "sorted": false,
                "unsorted": true
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
                  "fabricante": "General Motors",
                  "codigo": "94754484",
                  "precoEmCentavos": 11555,
                  "descricao": "ISOLADOR DE BORRACHA",
                  "dataDeAtualizacao": "2024-10-27T03:00:00.000+00:00",
                  "hash": "6c0dfcabe9f257766b98ab12ebe4ddc8"
                },
                {
                  "id": 431,
                  "fabricante": "Distribuidor",
                  "codigo": "94754484",
                  "precoEmCentavos": 10826,
                  "descricao": "\"Borracha Estabilizador Onix 2013 / 2 Peça Gm 9475",
                  "dataDeAtualizacao": "2024-10-27T03:00:00.000+00:00",
                  "hash": "ca2068bf97f98362cca9b0b1b9060c10"
                }
              ],
              "pageable": {
                "pageNumber": 0,
                "pageSize": 2,
                "sort": {
                  "empty": true,
                  "sorted": false,
                  "unsorted": true
                },
                "offset": 0,
                "paged": true,
                "unpaged": false
              },
              "last": false,
              "totalPages": 3,
              "totalElements": 5,
              "first": true,
              "size": 2,
              "number": 0,
              "sort": {
                "empty": true,
                "sorted": false,
                "unsorted": true
              },
              "numberOfElements": 2,
              "empty": false
            }
        """
    }
}