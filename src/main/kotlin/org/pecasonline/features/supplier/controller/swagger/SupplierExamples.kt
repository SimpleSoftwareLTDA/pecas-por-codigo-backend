package org.pecasonline.features.supplier.controller.swagger

object SupplierExamples {
    const val GET_ALL_SUPPLIERS_EXAMPLE = """
                    {
                        "content": [
                            {
                                "id": 1,
                                "nome": "Brasauto BH (BELO HORIZONTE-MG) 31,2109-3000",
                                "linkFornecedorOriginal": "http://www.pecas-on-line.com.br/empresas.php4?Empresa=Brasauto+BH",
                                "razaoSocial": "Foco Automoveis Ltda",
                                "descricao": {
                                    "id": 1,
                                    "descricao": "Concessionário"
                                },
                                "marca": {
                                    "id": 1,
                                    "marca": "Agip"
                                },
                                "cnpj": "10.376.703/0007-68",
                                "inscricaoEstadual": "10927340518",
                                "endereco": {
                                    "id": 1,
                                    "endereco": "AV RAJA GABAGLIA, 2440",
                                    "cidade": "BELO HORIZONTE",
                                    "estado": {
                                        "id": 13,
                                        "sigla": "MG",
                                        "nome": "Minas Gerais"
                                    },
                                    "cep": "30494-170",
                                    "pais": "Brasil"
                                },
                                "contato": {
                                    "id": 1,
                                    "vendedores": "Claudio, Wanderley",
                                    "email": "claudio@brasauto.com.br",
                                    "telefone": "31,2109-3000",
                                    "whatsapp": "31,98464-0818",
                                    "whatsappPecas": null,
                                    "emailEstoque": null,
                                    "emailContasPagar": null,
                                    "emailNotaFiscal": null,
                                    "webSite": "http://www.brasauto.com.br"
                                }
                            },
                            {
                                "id": 2,
                                "nome": "AN Pecas (Belo Horizonte-MG) 31,2512-1366",
                                "linkFornecedorOriginal": "http://www.pecas-on-line.com.br/empresas.php4?Empresa=AN+Pecas",
                                "razaoSocial": "AN PECAS E SERVICOS LTDA",
                                "descricao": null,
                                "marca": null,
                                "cnpj": "50.353.894/0001-71",
                                "inscricaoEstadual": "004596730.00-82",
                                "endereco": {
                                    "id": 2,
                                    "endereco": "RUA  ARACI 241",
                                    "cidade": "Belo Horizonte",
                                    "estado": {
                                        "id": 13,
                                        "sigla": "MG",
                                        "nome": "Minas Gerais"
                                    },
                                    "cep": "30865-200",
                                    "pais": "Brasil"
                                },
                                "contato": {
                                    "id": 2,
                                    "vendedores": "Pedro / Nilson",
                                    "email": "CONSERTAR4X4@HOTMAIL.COM",
                                    "telefone": "31,2512-1366",
                                    "whatsapp": "(31) 98903-4244",
                                    "whatsappPecas": null,
                                    "emailEstoque": null,
                                    "emailContasPagar": null,
                                    "emailNotaFiscal": null,
                                    "webSite": "Não encontrado"
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
                        "totalPages": 149,
                        "totalElements": 298,
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
                  "nome": "Brasauto BH (BELO HORIZONTE-MG) 31,2109-3000",
                  "linkFornecedorOriginal": "http://www.pecas-on-line.com.br/empresas.php4?Empresa=Brasauto+BH",
                  "razaoSocial": "Foco Automoveis Ltda",
                  "descricao": {
                    "id": 1,
                    "descricao": "Concessionário"
                  },
                  "marca": {
                    "id": 1,
                    "marca": "Agip"
                  },
                  "cnpj": "10.376.703/0007-68",
                  "inscricaoEstadual": "10927340518",
                  "endereco": {
                    "id": 1,
                    "endereco": "AV RAJA GABAGLIA, 2440",
                    "cidade": "BELO HORIZONTE",
                    "estado": {
                      "id": 13,
                      "sigla": "MG",
                      "nome": "Minas Gerais"
                    },
                    "cep": "30494-170",
                    "pais": "Brasil"
                  },
                  "contato": {
                    "id": 1,
                    "vendedores": "Claudio, Wanderley",
                    "email": "claudio@brasauto.com.br",
                    "telefone": "31,2109-3000",
                    "whatsapp": "31,98464-0818",
                    "whatsappPecas": null,
                    "emailEstoque": null,
                    "emailContasPagar": null,
                    "emailNotaFiscal": null,
                    "webSite": "http://www.brasauto.com.br"
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

    const val CREATE_SUPPLIER_REQUEST = """
            {
              "empresa": "Fornecedor Exemplo",
              "linkFornecedorOriginal": "https://example.com",
              "razaoSocial": "Fornecedor Exemplo LTDA",
              "cnpj": "15826705000130",
              "inscricao": "12345678",
              "idPlano": 3,
              "assinatura": {
                "diaPagamento": 15,
                "idPlano": 3,
                "bigBannerUrl": "https://example.com/banner-big.jpg",
                "smallBannerUrl": "https://example.com/banner-small.jpg"
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
              },
              "endereco": {
                "endereco": "Rua Exemplo",
                "cidade": "Cidade Exemplo",
                "cep": "12345678",
                "pais": "Brasil",
                "idEstado": 1
              }
            }
        """

    const val CREATE_SUPPLIER_ANSWER = """
            {
                "id": 301,
                "nome": "Fornecedor Exemplo",
                "linkFornecedorOriginal": null,
                "razaoSocial": "Fornecedor Exemplo LTDA",
                "cnpj": "15826705000130",
                "inscricaoEstadual": "12345678",
                "endereco": {
                    "id": 301,
                    "endereco": "Rua Exemplo",
                    "cidade": "Cidade Exemplo",
                    "estado": {
                        "id": 1,
                        "sigla": "AC",
                        "nome": "Acre"
                    },
                    "cep": "12345678",
                    "pais": "Brasil"
                },
                "contato": {
                    "id": 301,
                    "vendedores": "João Silva",
                    "email": "pecas@example.com",
                    "telefone": "1234567890",
                    "whatsapp": "1234567890",
                    "whatsappPecas": "0987654321",
                    "emailEstoque": "estoque@example.com",
                    "emailContasPagar": "contaspagar@example.com",
                    "emailNotaFiscal": "notafiscal@example.com",
                    "webSite": "https://fornecedorexemplo.com"
                }
            }
            """

    const val BAD_REQUEST = """
        {
            "razaoSocial": "A razão social do fornecedor não pode ser vazio",
            "cnpj": "O CNPJ do fornecedor é obrigatório",
            "empresa": "O nome do fornecedor é obrigatório",
            "inscricao": "A inscrição estadual do fornecedor não pode ser vazio",
            "contato": "O contato do fornecedor é obrigatório",
            "endereco": "O endereço do fornecedor é obrigatório",
            "assinatura": "A assinatura do fornecedor é obrigatória",
            "idPlano": "O ID do plano do fornecedor é obrigatório"
        }
        """

}