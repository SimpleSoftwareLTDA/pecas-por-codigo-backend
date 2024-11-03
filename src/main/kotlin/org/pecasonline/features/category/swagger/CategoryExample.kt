package org.pecasonline.features.category.swagger

class CategoryExample {
    companion object {
        const val GET_CATEGORIES = """
            {
                "content": [
                    {
                        "id": 1,
                        "nome": "Junta-cabecote"
                    },
                    {
                        "id": 2,
                        "nome": "Limpador"
                    },
                    {
                        "id": 3,
                        "nome": "Pedaleiras"
                    },
                    {
                        "id": 4,
                        "nome": "1.4"
                    },
                    {
                        "id": 5,
                        "nome": "Casq"
                    },
                    {
                        "id": 6,
                        "nome": "Retentor(skf"
                    },
                    {
                        "id": 7,
                        "nome": "15a"
                    },
                    {
                        "id": 185,
                        "nome": "Secador"
                    },
                    {
                        "id": 186,
                        "nome": "Painelm"
                    },
                    {
                        "id": 187,
                        "nome": "Parafuso"
                    }
                ],
                "pageable": {
                    "pageNumber": 0,
                    "pageSize": 10,
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
                "totalElements": 708,
                "totalPages": 71,
                "first": true,
                "size": 10,
                "number": 0,
                "sort": {
                    "empty": true,
                    "unsorted": true,
                    "sorted": false
                },
                "numberOfElements": 10,
                "empty": false
            }
        """

        const val GET_CATEGORY_BY_ID = """
             {
                "id": 1,
                "nome": "Junta-cabecote"
            }
        """

        const val GET_DESCRIPTION_BY_SEARCH_NAME = """
            {
                "content": [
                    {
                        "id": 1,
                        "nome": "Junta-cabecote"
                    },
                    {
                        "id": 2,
                        "nome": "Limpador"
                    },
                    {
                        "id": 3,
                        "nome": "Pedaleiras"
                    },
                    {
                        "id": 4,
                        "nome": "1.4"
                    },
                    {
                        "id": 5,
                        "nome": "Casq"
                    },
                    {
                        "id": 6,
                        "nome": "Retentor(skf"
                    },
                    {
                        "id": 7,
                        "nome": "15a"
                    },
                    {
                        "id": 185,
                        "nome": "Secador"
                    },
                    {
                        "id": 186,
                        "nome": "Painelm"
                    },
                    {
                        "id": 187,
                        "nome": "Parafuso"
                    }
                ],
                "pageable": {
                    "pageNumber": 0,
                    "pageSize": 10,
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
                "totalElements": 708,
                "totalPages": 71,
                "first": true,
                "size": 10,
                "number": 0,
                "sort": {
                    "empty": true,
                    "unsorted": true,
                    "sorted": false
                },
                "numberOfElements": 10,
                "empty": false
            }
        """
    }
}