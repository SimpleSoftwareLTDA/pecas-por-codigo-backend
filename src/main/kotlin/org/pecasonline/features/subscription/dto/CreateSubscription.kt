package org.pecasonline.features.subscription.dto

import com.fasterxml.jackson.annotation.JsonAlias
import jakarta.validation.constraints.NotNull
import org.pecasonline.features.plan.Plan
import org.pecasonline.features.subscription.entities.Subscription
import org.pecasonline.features.supplier.domain.Supplier

data class CreateSubscription(
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