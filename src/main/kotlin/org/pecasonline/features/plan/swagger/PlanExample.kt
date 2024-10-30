package org.pecasonline.features.brand.swagger

class PlanExample {
    companion object {
        const val GET_PLANS = """
           [
                {
                    "id": 1,
                    "nome": "Plano Básico",
                    "precoEmCentavos": 52100,
                    "estoque": true,
                    "descricao": true,
                    "bannerPequeno": false,
                    "bannerGrande": false
                },
                {
                    "id": 2,
                    "nome": "Plano Premium",
                    "precoEmCentavos": 78300,
                    "estoque": true,
                    "descricao": true,
                    "bannerPequeno": true,
                    "bannerGrande": false
                },
                {
                    "id": 3,
                    "nome": "Plano VIP",
                    "precoEmCentavos": 130800,
                    "estoque": true,
                    "descricao": true,
                    "bannerPequeno": false,
                    "bannerGrande": true
                }
            ]
        """
    }
}