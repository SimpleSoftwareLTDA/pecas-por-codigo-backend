package org.pecasonline.features.plan.swagger

object PlanExample {
    const val GET_PLANS = """
           [
                {
                    "id": 1,
                    "nome": "Plano Básico",
                    "precoEmCentavos": 29999,
                    "estoque": true,
                    "descricao": true,
                    "bannerPequeno": false,
                    "bannerGrande": false
                },
                {
                    "id": 3,
                    "nome": "Plano VIP",
                    "precoEmCentavos": 39999,
                    "estoque": true,
                    "descricao": true,
                    "bannerPequeno": false,
                    "bannerGrande": true
                }
            ]
        """
}