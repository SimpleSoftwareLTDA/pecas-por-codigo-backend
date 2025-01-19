package org.pecasonline.features.subscription.constants

enum class SubscriptionPlan(val id: Int, val price: Double, val description: String) {
    BASIC(1, 299.99, "Plano Básico - Apenas permite subir o estoque para o catálogo"),
    VIP(2, 399.99, "Plano Intermediário - Além do básico, permite expor um banner de propaganda no site.");

    companion object {
        fun nameFromId(id: Int): String =
            entries.find { it.id == id }?.name
                ?: throw IllegalArgumentException("Plano de assinatura inválido para o ID: $id")

        fun priceFromId(id: Int): Double =
            entries.find { it.id == id }?.price
                ?: throw IllegalArgumentException("Plano de assinatura inválido para o ID: $id")
    }
}