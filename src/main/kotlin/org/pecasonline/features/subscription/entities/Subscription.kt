package org.pecasonline.features.subscription.entities

import com.fasterxml.jackson.annotation.JsonAlias
import com.fasterxml.jackson.annotation.JsonManagedReference
import com.fasterxml.jackson.annotation.JsonProperty
import jakarta.persistence.*
import org.pecasonline.features.plan.Plan
import org.pecasonline.features.supplier.domain.Supplier

@Entity(name = "signature")
data class Subscription(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @JsonProperty("diaPagamento")
    @JsonAlias(value = ["payment_day", "paymentDay"])
    val paymentDay: Int,

    @OneToOne
    @JoinColumn(name = "supplier_id", nullable = false)
    @JsonProperty("fornecedor")
    @JsonAlias("supplier")
    @JsonManagedReference
    val supplier: Supplier,

    @ManyToOne
    @JoinColumn(name = "plan_id", nullable = false)
    @JsonProperty("plano")
    @JsonAlias("plan")
    val plan: Plan,

    @JsonProperty("bannerGrandeUrl")
    @JsonAlias(value = ["big_banner_url", "bigBannerUrl"])
    val bigBannerUrl: String? = null,

    @JsonProperty("bannerPequenoUrl")
    @JsonAlias(value = ["small_banner_url", "smallBannerUrl"])
    val smallBannerUrl: String? = null,

    @Enumerated(EnumType.STRING)
    @JsonProperty("status")
    var status: SubscriptionStatus = SubscriptionStatus.INACTIVE
)

enum class SubscriptionStatus(val description: String) {
    ACTIVE("Assinatura ativa"),
    INACTIVE("Assinatura inativa"),
    LATE("Assinatura atrasada");
}

class InvalidTokenException : RuntimeException("Token inválido")
class SupplierNotFoundException : RuntimeException("Fornecedor não encontrado")
class SubscriptionInactiveException : RuntimeException("O primeiro pagamento não foi realizado")
class InvalidSubscriptionException : RuntimeException("Assinatura inválida")
class PaymentLateException : RuntimeException("Pagamento atrasado")
