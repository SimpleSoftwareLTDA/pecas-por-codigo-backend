package org.pecasonline.features.category.swagger

class CategoryExample {
    companion object {
        const val GET_CATEGORIES = """
            {
                "content": [
                    {
                        "id": 1,
                        "categoria": "Junta-cabecote"
                    },
                    {
                        "id": 2,
                        "categoria": "Limpador"
                    },
                    {
                        "id": 3,
                        "categoria": "Pedaleiras"
                    },
                    {
                        "id": 4,
                        "categoria": "1.4"
                    },
                    {
                        "id": 5,
                        "categoria": "Casq"
                    },
                    {
                        "id": 6,
                        "categoria": "Retentor(skf"
                    },
                    {
                        "id": 7,
                        "categoria": "15a"
                    },
                    {
                        "id": 185,
                        "categoria": "Secador"
                    },
                    {
                        "id": 186,
                        "categoria": "Painelm"
                    },
                    {
                        "id": 187,
                        "categoria": "Parafuso"
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
                "categoria": "Junta-cabecote"
            }
        """

        const val GET_DESCRIPTION_BY_SEARCH_NAME = """
            {
                "content": [
                    {
                        "id": 1,
                        "categoria": "Junta-cabecote"
                    },
                    {
                        "id": 2,
                        "categoria": "Limpador"
                    },
                    {
                        "id": 3,
                        "categoria": "Pedaleiras"
                    },
                    {
                        "id": 4,
                        "categoria": "1.4"
                    },
                    {
                        "id": 5,
                        "categoria": "Casq"
                    },
                    {
                        "id": 6,
                        "categoria": "Retentor(skf"
                    },
                    {
                        "id": 7,
                        "categoria": "15a"
                    },
                    {
                        "id": 185,
                        "categoria": "Secador"
                    },
                    {
                        "id": 186,
                        "categoria": "Painelm"
                    },
                    {
                        "id": 187,
                        "categoria": "Parafuso"
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