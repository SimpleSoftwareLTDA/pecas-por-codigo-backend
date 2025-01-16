package org.pecasonline.features.subscription

import com.fasterxml.jackson.annotation.JsonAlias
import jakarta.validation.constraints.NotNull
import org.pecasonline.features.plan.Plan
import org.pecasonline.features.supplier.domain.Supplier

data class CreateSubscriptionDTO(
    @field:NotNull(message = "O dia de pagamento é obrigatório")
    @JsonAlias("diaPagamento")
    val paymentDay: Int,

    @field:NotNull(message = "O ID do plano é obrigatório")
    @JsonAlias("idPlano")
    val planId: Int,

    val bigBannerUrl: String? = null,
    val smallBannerUrl: String? = null
) {
    fun toSubscription(supplier: Supplier, plan: Plan) = Subscription(
        paymentDay = paymentDay,
        supplier = supplier,
        plan = plan,
        bigBannerUrl = bigBannerUrl,
        smallBannerUrl = smallBannerUrl
    )
}

enum class SubscriptionPlan(val id: Int, val price: Double, val description: String) {
    BASIC(1, 100.0, "Plano Básico - Ideal para iniciantes"),
    INTERMEDIATE(2, 200.0, "Plano Intermediário - Para quem busca mais recursos"),
    ADVANCED(3, 300.0, "Plano Avançado - Completo e avançado");

    companion object {
        fun nameFromId(id: Int): String =
            entries.find { it.id == id }?.name
                ?: throw IllegalArgumentException("Plano de assinatura inválido para o ID: $id")

        fun priceFromId(id: Int): Double =
            entries.find { it.id == id }?.price
                ?: throw IllegalArgumentException("Plano de assinatura inválido para o ID: $id")
    }
}


