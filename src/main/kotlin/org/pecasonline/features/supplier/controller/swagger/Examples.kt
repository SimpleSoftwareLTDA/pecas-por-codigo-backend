package org.pecasonline.features.supplier.controller.swagger

class Examples {
    companion object {
        const val GET_ALL_SUPPLIERS_EXAMPLE = """
                    {
                        "content": [
                            {
                                "id": 1,
                                "name": "Brasauto BH (BELO HORIZONTE-MG) 31,2109-3000",
                                "supplierOriginalLink": "http://www.pecas-on-line.com.br/empresas.php4?Empresa=Brasauto+BH",
                                "socialName": "Foco Automoveis Ltda",
                                  "description": {
                                    "id": 1,
                                    "text": "Supplier description here"
                                  },
                                  "brand": {
                                    "id": 1,
                                    "brandName": "Brand 1"
                                  },
                                "cnpj": "10.376.703/0007-68",
                                "stateSubscription": "10927340518",
                                "address": {
                                    "id": 1,
                                    "street": "AV RAJA GABAGLIA, 2440",
                                    "city": "BELO HORIZONTE",
                                    "state": {
                                        "id": 13,
                                        "stateCode": "MG",
                                        "stateName": "Minas Gerais"
                                    },
                                    "cep": "30494-170",
                                    "country": "Brasil"
                                },
                                "contact": {
                                    "id": 1,
                                    "sellerName": "Claudio, Wanderley",
                                    "itemsEmail": "claudio@brasauto.com.br",
                                    "itemsPhone": "31,2109-3000",
                                    "whatsapp": "31,98464-0818",
                                    "itemsWhatsapp": null,
                                    "stockEmail": null,
                                    "billingEmail": null,
                                    "nfEmail": null,
                                    "site": "http://www.brasauto.com.br"
                                }
                            },
                            {
                                "id": 2,
                                "name": "AN Pecas (Belo Horizonte-MG) 31,2512-1366",
                                "supplierOriginalLink": "http://www.pecas-on-line.com.br/empresas.php4?Empresa=AN+Pecas",
                                "socialName": "AN PECAS E SERVICOS LTDA",
                                "description": null,
                                "brand": null,
                                "cnpj": "50.353.894/0001-71",
                                "stateSubscription": "004596730.00-82",
                                "address": {
                                    "id": 2,
                                    "street": "RUA  ARACI 241",
                                    "city": "Belo Horizonte",
                                    "state": {
                                        "id": 13,
                                        "stateCode": "MG",
                                        "stateName": "Minas Gerais"
                                    },
                                    "cep": "30865-200",
                                    "country": "Brasil"
                                },
                                "contact": {
                                    "id": 2,
                                    "sellerName": "Pedro / Nilson",
                                    "itemsEmail": "CONSERTAR4X4@HOTMAIL.COM",
                                    "itemsPhone": "31,2512-1366",
                                    "whatsapp": "(31) 98903-4244",
                                    "itemsWhatsapp": null,
                                    "stockEmail": null,
                                    "billingEmail": null,
                                    "nfEmail": null,
                                    "site": "Não encontrado"
                                }
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
                        "totalElements": 298,
                        "totalPages": 149,
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

        const val GET_SUPPLIER_BY_ID =
            """
                {
                    "id": 1,
                    "name": "Brasauto BH (BELO HORIZONTE-MG) 31,2109-3000",
                    "supplierOriginalLink": "http://www.pecas-on-line.com.br/empresas.php4?Empresa=Brasauto+BH",
                    "socialName": "Foco Automoveis Ltda",
                    "description": null,
                    "brand": null,
                    "cnpj": "10.376.703/0007-68",
                    "stateSubscription": "10927340518",
                    "address": {
                        "id": 1,
                        "street": "AV RAJA GABAGLIA, 2440",
                        "city": "BELO HORIZONTE",
                        "state": {
                            "id": 13,
                            "stateCode": "MG",
                            "stateName": "Minas Gerais"
                        },
                        "cep": "30494-170",
                        "country": "Brasil"
                    },
                    "contact": {
                        "id": 1,
                        "sellerName": "Claudio, Wanderley",
                        "itemsEmail": "claudio@brasauto.com.br",
                        "itemsPhone": "31,2109-3000",
                        "whatsapp": "31,98464-0818",
                        "itemsWhatsapp": null,
                        "stockEmail": null,
                        "billingEmail": null,
                        "nfEmail": null,
                        "site": "http://www.brasauto.com.br"
                    }
                }
    """

        const val SUPPLIER_NOT_FOUND = """
            {
                "httpStatusCode": 404,
                "message": "Fornecedor não encontrado."
            }
        """

        const val INTERNAL_SERVER_ERROR = """
        {
            "timestamp": "2024-10-30T03:42:58.933+00:00",
            "status": 500,
            "error": "Internal Server Error",
            "path": "/suppliers/1"
        }
    """

        const val CREATE_SUPPLIER = """
            {
              "empresa": "Fornecedor Exemplo",
              "supplierOriginalLink": "https://example.com",
              "razaoSocial": "Fornecedor Exemplo LTDA",
              "cnpj": "12345678901234",
              "inscricao": "12345678",
              "idDescricao": 1,
              "idMarca": 2,
              "idPlano": 3,
              "assinatura": {
                "diaPagamento": 15,
                "idPlano": 3,
                "bigBannerUrl": "https://example.com/banners/big.jpg",
                "smallBannerUrl": "https://example.com/banners/small.jpg"
              },
              "endereco": {
                "street": "Rua Exemplo",
                "city": "Cidade Exemplo",
                "state": "Estado Exemplo",
                "cep": "12345678",
                "country": "Brasil"
              },
              "contato": {
                "vendedores": "João Silva",
                "emailPecas": "pecas@example.com",
                "fonePecas": "1234567890",
                "whatsappGeral": "1234567890",
                "whatsappPecas": "0987654321",
                "emailEstoque": "estoque@example.com",
                "emailContasPagar": "contaspagar@example.com",
                "emailNotaFiscal": "notafiscal@example.com",
                "website": "https://fornecedorexemplo.com"
              }
            }
            """

        const val BAD_REQUEST = """
        {
            "razaoSocial": "A razão social do fornecedor não pode ser vazio",
            "cnpj": "O CNPJ do fornecedor é obrigatório",
            "empresa": "O nome do fornecedor é obrigatório",
            "inscricao": "A inscrição estadual do fornecedor não pode ser vazio",
            "idMarca": "O ID da marca do fornecedor é obrigatório",
            "contato": "O contato do fornecedor é obrigatório",
            "endereco": "O endereço do fornecedor é obrigatório",
            "idDescricao": "O ID da descrição do fornecedor é obrigatório",
            "assinatura": "A assinatura do fornecedor é obrigatória",
            "idPlano": "O ID do plano do fornecedor é obrigatório"
        }
        """
    }
}