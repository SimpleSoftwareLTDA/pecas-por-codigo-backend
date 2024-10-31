package org.pecasonline.features.stock.swagger

class StockExamples {
    companion object {
        const val GET_ALL_STOCKS = """
            {
              "content": [
                {
                  "id": 354,
                  "quantity": 1,
                  "supplier": {
                    "id": 148,
                    "nome": "BSB (Brasilis-DF) 61,9646-3671",
                    "linkFornecedorOriginal": "http://www.pecas-on-line.com.br/empresas.php4?Empresa=BSB",
                    "razaoSocial": "C.S LOPES LTDA",
                    "descricao": null,
                    "marca": null,
                    "cnpj": "52193298000105",
                    "inscricaoEstadual": "0824868900107",
                    "endereco": {
                      "id": 148,
                      "endereco": "SETOR SH VICENTE PIRES Q 8 CJ 2 LT 8",
                      "cidade": "Brasilis",
                      "estado": {
                        "id": 7,
                        "sigla": "DF",
                        "nome": "Distrito Federal"
                      },
                      "cep": "72001-799",
                      "pais": "Brasil"
                    },
                    "contato": {
                      "id": 148,
                      "vendedores": "Wilian",
                      "email": "bsbautopartsdf@gmail.com",
                      "telefone": "61,9646-3671",
                      "whatsapp": "61,9646-3671",
                      "whatsappPecas": null,
                      "emailEstoque": null,
                      "emailContasPagar": null,
                      "emailNotaFiscal": null,
                      "webSite": "http://"
                    }
                  },
                  "item": {
                    "id": 232,
                    "manufacturer": "Distribuidor",
                    "code": "52046262",
                    "priceInCents": null,
                    "description": "FILTRO",
                    "updateDate": "2024-10-27T03:00:00.000+00:00",
                    "hash": "d3b3d22af71cb9992b7001f0448301e6"
                  }
                },
                {
                  "id": 355,
                  "quantity": 3,
                  "supplier": {
                    "id": 76,
                    "nome": "Utilpecas (Belo Horizonte-MG) 31,3491-5210 / 0992",
                    "linkFornecedorOriginal": "http://www.pecas-on-line.com.br/empresas.php4?Empresa=Utilpecas",
                    "razaoSocial": "Utilpecas Ltda",
                    "descricao": null,
                    "marca": null,
                    "cnpj": "01.095.324/0001-13",
                    "inscricaoEstadual": "062.964425.0086",
                    "endereco": {
                      "id": 76,
                      "endereco": "Rua Major Delfino de Paula 2595",
                      "cidade": "Belo Horizonte",
                      "estado": {
                        "id": 13,
                        "sigla": "MG",
                        "nome": "Minas Gerais"
                      },
                      "cep": "31255-170",
                      "pais": "Brasil"
                    },
                    "contato": {
                      "id": 76,
                      "vendedores": "ADAIR// TONINHO /// JADIR //JOSE ANTÔNIO",
                      "email": "utilpecas@zipmail.com.br",
                      "telefone": "31,3491-5210 / 0992",
                      "whatsapp": "31,98634-9676",
                      "whatsappPecas": null,
                      "emailEstoque": null,
                      "emailContasPagar": null,
                      "emailNotaFiscal": null,
                      "webSite": "Não encontrado"
                    }
                  },
                  "item": {
                    "id": 233,
                    "manufacturer": "Distribuidor",
                    "code": "12581701",
                    "priceInCents": null,
                    "description": "POLIA TENSORA S10 2012/",
                    "updateDate": "2024-10-27T03:00:00.000+00:00",
                    "hash": "14ea96e6b5528a5d1fffe55c7bc6f225"
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
              "totalPages": 22905,
              "totalElements": 45809,
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

        const val GET_STOCK_BY_ID = """
            {
              "id": 355,
              "quantity": 3,
              "supplier": {
                "id": 76,
                "nome": "Utilpecas (Belo Horizonte-MG) 31,3491-5210 / 0992",
                "linkFornecedorOriginal": "http://www.pecas-on-line.com.br/empresas.php4?Empresa=Utilpecas",
                "razaoSocial": "Utilpecas Ltda",
                "descricao": null,
                "marca": null,
                "cnpj": "01.095.324/0001-13",
                "inscricaoEstadual": "062.964425.0086",
                "endereco": {
                  "id": 76,
                  "endereco": "Rua Major Delfino de Paula 2595",
                  "cidade": "Belo Horizonte",
                  "estado": {
                    "id": 13,
                    "sigla": "MG",
                    "nome": "Minas Gerais"
                  },
                  "cep": "31255-170",
                  "pais": "Brasil"
                },
                "contato": {
                  "id": 76,
                  "vendedores": "ADAIR// TONINHO /// JADIR //JOSE ANTÔNIO",
                  "email": "utilpecas@zipmail.com.br",
                  "telefone": "31,3491-5210 / 0992",
                  "whatsapp": "31,98634-9676",
                  "whatsappPecas": null,
                  "emailEstoque": null,
                  "emailContasPagar": null,
                  "emailNotaFiscal": null,
                  "webSite": "Não encontrado"
                }
              },
              "item": {
                "id": 233,
                "manufacturer": "Distribuidor",
                "code": "12581701",
                "priceInCents": null,
                "description": "POLIA TENSORA S10 2012/",
                "updateDate": "2024-10-27T03:00:00.000+00:00",
                "hash": "14ea96e6b5528a5d1fffe55c7bc6f225"
              }
            }
        """

        const val STOCK_NOT_FOUND = """
            {
              "httpStatusCode": 404,
              "message": "Estoque não encontrado"
            }
        """

        const val GET_STOCK_BY_ITEM_ID = """
            {
              "totalPages": 0,
              "totalElements": 0,
              "first": true,
              "last": true,
              "size": 0,
              "content": [
                {
                  "id": 0,
                  "quantity": 0,
                  "supplier": {
                    "id": 0,
                    "nome": "string",
                    "linkFornecedorOriginal": "string",
                    "razaoSocial": "string",
                    "descricao": {
                      "id": 0,
                      "descricao": "string"
                    },
                    "marca": {
                      "id": 0,
                      "marca": "string"
                    },
                    "cnpj": "string",
                    "inscricaoEstadual": "string",
                    "endereco": {
                      "id": 0,
                      "endereco": "string",
                      "cidade": "string",
                      "estado": {
                        "id": 0,
                        "sigla": "string",
                        "nome": "string"
                      },
                      "cep": "string",
                      "pais": "string"
                    },
                    "contato": {
                      "id": 0,
                      "vendedores": "string",
                      "email": "string",
                      "telefone": "string",
                      "whatsapp": "string",
                      "whatsappPecas": "string",
                      "emailEstoque": "string",
                      "emailContasPagar": "string",
                      "emailNotaFiscal": "string",
                      "webSite": "string"
                    }
                  },
                  "item": {
                    "id": 0,
                    "manufacturer": "string",
                    "code": "string",
                    "priceInCents": 0,
                    "description": "string",
                    "updateDate": "2024-10-30T23:10:19.792Z",
                    "hash": "string"
                  }
                }
              ],
              "number": 0,
              "sort": [
                {
                  "direction": "string",
                  "nullHandling": "string",
                  "ascending": true,
                  "property": "string",
                  "ignoreCase": true
                }
              ],
              "pageable": {
                "offset": 0,
                "sort": [
                  {
                    "direction": "string",
                    "nullHandling": "string",
                    "ascending": true,
                    "property": "string",
                    "ignoreCase": true
                  }
                ],
                "paged": true,
                "pageNumber": 0,
                "pageSize": 0,
                "unpaged": true
              },
              "numberOfElements": 0,
              "empty": true
            }
        """

        const val GET_STOCK_BY_SUPPLIER_NAME = """
            {
              "content": [
                {
                  "id": 359,
                  "quantity": 1,
                  "supplier": {
                    "id": 260,
                    "nome": "Auto Pecas Parana (UBIRATA-PR) 44,35431054",
                    "linkFornecedorOriginal": "http://www.pecas-on-line.com.br/empresas.php4?Empresa=Auto+Pecas+Parana",
                    "razaoSocial": "J J DE OLIVEIRA AUTOMOTIVOS Ltda",
                    "descricao": null,
                    "marca": null,
                    "cnpj": "82.219.932.0001.94",
                    "inscricaoEstadual": "8120187369",
                    "endereco": {
                      "id": 260,
                      "endereco": "RUA NOSSA SENHORA APARECIDA  748",
                      "cidade": "UBIRATA",
                      "estado": {
                        "id": 16,
                        "sigla": "PR",
                        "nome": "Paraná"
                      },
                      "cep": "854400-000",
                      "pais": "Brasil"
                    },
                    "contato": {
                      "id": 260,
                      "vendedores": "Oliveira",
                      "email": "OLIVEIRAAPARECIDO3912@HOTMAIL.COM",
                      "telefone": "44,35431054",
                      "whatsapp": "44,99990-1944",
                      "whatsappPecas": null,
                      "emailEstoque": null,
                      "emailContasPagar": null,
                      "emailNotaFiscal": null,
                      "webSite": "http://www.autopecasparanaubirata.com.br"
                    }
                  },
                  "item": {
                    "id": 235,
                    "manufacturer": "Distribuidor",
                    "code": "52046262",
                    "priceInCents": 15631,
                    "description": "\"Elemento Do Filtro De Ar S10 Trailblazer Gm 52046",
                    "updateDate": "2024-10-27T03:00:00.000+00:00",
                    "hash": "d60946b17edcd470e1639ee7f3fd33bf"
                  }
                },
                {
                  "id": 364,
                  "quantity": 1,
                  "supplier": {
                    "id": 73,
                    "nome": "Casarao Auto Pecas (Franca-SP) 16,3705-1400",
                    "linkFornecedorOriginal": "http://www.pecas-on-line.com.br/empresas.php4?Empresa=Casarao+Auto+Pecas",
                    "razaoSocial": "Casarao Auto Pecas Ltda",
                    "descricao": null,
                    "marca": null,
                    "cnpj": "19.390.972/0001-68",
                    "inscricaoEstadual": "310.531.075-114",
                    "endereco": {
                      "id": 73,
                      "endereco": "Rua Fued Zacarias Cury, 849",
                      "cidade": "Franca",
                      "estado": {
                        "id": 25,
                        "sigla": "SP",
                        "nome": "São Paulo"
                      },
                      "cep": "14403-088",
                      "pais": "Brasil"
                    },
                    "contato": {
                      "id": 73,
                      "vendedores": "Julio, Mauro, Ronaldo, Pedro",
                      "email": "financeiro@casaraoautopecas.com.br",
                      "telefone": "16,3705-1400",
                      "whatsapp": "16,3705-1400",
                      "whatsappPecas": null,
                      "emailEstoque": null,
                      "emailContasPagar": null,
                      "emailNotaFiscal": null,
                      "webSite": "http://www.casaraoautopecas.com.br"
                    }
                  },
                  "item": {
                    "id": 240,
                    "manufacturer": "Distribuidor",
                    "code": "93263176",
                    "priceInCents": null,
                    "description": "PARALAMA DIANT DIR",
                    "updateDate": "2024-10-27T03:00:00.000+00:00",
                    "hash": "98770a1e145ad3fc25a486c4317e5fc4"
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
              "totalPages": 1818,
              "totalElements": 3635,
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

        const val GET_STOCK_BY_SUPPLIER_ID = """
            {
              "content": [
                {
                  "id": 21017,
                  "quantity": 1,
                  "supplier": {
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
                  "item": {
                    "id": 7643,
                    "manufacturer": "Ford",
                    "code": "6S6Z15520A",
                    "priceInCents": 8255,
                    "description": "INTERRUPTOR",
                    "updateDate": "2024-10-27T03:00:00.000+00:00",
                    "hash": "9718fbb2a74d360b921f25b1c7e2b059"
                  }
                },
                {
                  "id": 30414,
                  "quantity": 14,
                  "supplier": {
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
                  "item": {
                    "id": 9917,
                    "manufacturer": "Ford",
                    "code": "CM5Z6K254A",
                    "priceInCents": 30184,
                    "description": "POLIA TENSORA",
                    "updateDate": "2024-10-27T03:00:00.000+00:00",
                    "hash": "665d1a96fd826e4dc02e797962b91c75"
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
              "last": true,
              "totalPages": 1,
              "totalElements": 2,
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

        const val GET_STOCK_BY_DESCRIPTION = """
            {
              "content": [
                {
                  "id": 20342,
                  "quantity": 1,
                  "supplier": {
                    "id": 260,
                    "nome": "Auto Pecas Parana (UBIRATA-PR) 44,35431054",
                    "linkFornecedorOriginal": "http://www.pecas-on-line.com.br/empresas.php4?Empresa=Auto+Pecas+Parana",
                    "razaoSocial": "J J DE OLIVEIRA AUTOMOTIVOS Ltda",
                    "descricao": null,
                    "marca": null,
                    "cnpj": "82.219.932.0001.94",
                    "inscricaoEstadual": "8120187369",
                    "endereco": {
                      "id": 260,
                      "endereco": "RUA NOSSA SENHORA APARECIDA  748",
                      "cidade": "UBIRATA",
                      "estado": {
                        "id": 16,
                        "sigla": "PR",
                        "nome": "Paraná"
                      },
                      "cep": "854400-000",
                      "pais": "Brasil"
                    },
                    "contato": {
                      "id": 260,
                      "vendedores": "Oliveira",
                      "email": "OLIVEIRAAPARECIDO3912@HOTMAIL.COM",
                      "telefone": "44,35431054",
                      "whatsapp": "44,99990-1944",
                      "whatsappPecas": null,
                      "emailEstoque": null,
                      "emailContasPagar": null,
                      "emailNotaFiscal": null,
                      "webSite": "http://www.autopecasparanaubirata.com.br"
                    }
                  },
                  "item": {
                    "id": 7491,
                    "manufacturer": "Distribuidor",
                    "code": "15656162",
                    "priceInCents": 66737,
                    "description": "\"Suporte C/buchas Do Diferencial Diant S10 4×4 Pc",
                    "updateDate": "2024-10-27T03:00:00.000+00:00",
                    "hash": "89aead95bbd2bf2283ceccad60f32594"
                  }
                },
                {
                  "id": 45185,
                  "quantity": 1,
                  "supplier": {
                    "id": 260,
                    "nome": "Auto Pecas Parana (UBIRATA-PR) 44,35431054",
                    "linkFornecedorOriginal": "http://www.pecas-on-line.com.br/empresas.php4?Empresa=Auto+Pecas+Parana",
                    "razaoSocial": "J J DE OLIVEIRA AUTOMOTIVOS Ltda",
                    "descricao": null,
                    "marca": null,
                    "cnpj": "82.219.932.0001.94",
                    "inscricaoEstadual": "8120187369",
                    "endereco": {
                      "id": 260,
                      "endereco": "RUA NOSSA SENHORA APARECIDA  748",
                      "cidade": "UBIRATA",
                      "estado": {
                        "id": 16,
                        "sigla": "PR",
                        "nome": "Paraná"
                      },
                      "cep": "854400-000",
                      "pais": "Brasil"
                    },
                    "contato": {
                      "id": 260,
                      "vendedores": "Oliveira",
                      "email": "OLIVEIRAAPARECIDO3912@HOTMAIL.COM",
                      "telefone": "44,35431054",
                      "whatsapp": "44,99990-1944",
                      "whatsappPecas": null,
                      "emailEstoque": null,
                      "emailContasPagar": null,
                      "emailNotaFiscal": null,
                      "webSite": "http://www.autopecasparanaubirata.com.br"
                    }
                  },
                  "item": {
                    "id": 7491,
                    "manufacturer": "Distribuidor",
                    "code": "15656162",
                    "priceInCents": 66737,
                    "description": "\"Suporte C/buchas Do Diferencial Diant S10 4×4 Pc",
                    "updateDate": "2024-10-27T03:00:00.000+00:00",
                    "hash": "89aead95bbd2bf2283ceccad60f32594"
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
              "last": true,
              "totalPages": 1,
              "totalElements": 2,
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

        const val GET_STOCK_BY_ITEM_CODE = """
            {
              "content": [
                {
                  "id": 354,
                  "quantity": 1,
                  "supplier": {
                    "id": 148,
                    "nome": "BSB (Brasilis-DF) 61,9646-3671",
                    "linkFornecedorOriginal": "http://www.pecas-on-line.com.br/empresas.php4?Empresa=BSB",
                    "razaoSocial": "C.S LOPES LTDA",
                    "descricao": null,
                    "marca": null,
                    "cnpj": "52193298000105",
                    "inscricaoEstadual": "0824868900107",
                    "endereco": {
                      "id": 148,
                      "endereco": "SETOR SH VICENTE PIRES Q 8 CJ 2 LT 8",
                      "cidade": "Brasilis",
                      "estado": {
                        "id": 7,
                        "sigla": "DF",
                        "nome": "Distrito Federal"
                      },
                      "cep": "72001-799",
                      "pais": "Brasil"
                    },
                    "contato": {
                      "id": 148,
                      "vendedores": "Wilian",
                      "email": "bsbautopartsdf@gmail.com",
                      "telefone": "61,9646-3671",
                      "whatsapp": "61,9646-3671",
                      "whatsappPecas": null,
                      "emailEstoque": null,
                      "emailContasPagar": null,
                      "emailNotaFiscal": null,
                      "webSite": "http://"
                    }
                  },
                  "item": {
                    "id": 232,
                    "manufacturer": "Distribuidor",
                    "code": "52046262",
                    "priceInCents": null,
                    "description": "FILTRO",
                    "updateDate": "2024-10-27T03:00:00.000+00:00",
                    "hash": "d3b3d22af71cb9992b7001f0448301e6"
                  }
                },
                {
                  "id": 356,
                  "quantity": 1,
                  "supplier": {
                    "id": 95,
                    "nome": "DM (Contagem-MG) 31,99568-7324",
                    "linkFornecedorOriginal": "http://www.pecas-on-line.com.br/empresas.php4?Empresa=DM",
                    "razaoSocial": "DM COMERCIO E DISTIRBUDORA DE PECAS LTDA",
                    "descricao": null,
                    "marca": null,
                    "cnpj": "52.493.447/0001-43",
                    "inscricaoEstadual": "47346180089",
                    "endereco": {
                      "id": 95,
                      "endereco": "AV CORONEL JOVE SOARES NOGUEIRA 677",
                      "cidade": "Contagem",
                      "estado": {
                        "id": 13,
                        "sigla": "MG",
                        "nome": "Minas Gerais"
                      },
                      "cep": "32260-470",
                      "pais": "Brasil"
                    },
                    "contato": {
                      "id": 95,
                      "vendedores": "wilian",
                      "email": "dmautoparts35@gmail.com",
                      "telefone": "31,99568-7324",
                      "whatsapp": "31,99568-7324",
                      "whatsappPecas": null,
                      "emailEstoque": null,
                      "emailContasPagar": null,
                      "emailNotaFiscal": null,
                      "webSite": "http://"
                    }
                  },
                  "item": {
                    "id": 232,
                    "manufacturer": "Distribuidor",
                    "code": "52046262",
                    "priceInCents": null,
                    "description": "FILTRO",
                    "updateDate": "2024-10-27T03:00:00.000+00:00",
                    "hash": "d3b3d22af71cb9992b7001f0448301e6"
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
              "totalPages": 20,
              "totalElements": 40,
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