package org.pecasonline.features.subscription

import com.fasterxml.jackson.annotation.JsonAlias
import com.fasterxml.jackson.annotation.JsonProperty
import jakarta.persistence.*
import org.pecasonline.features.plan.Plan
import org.pecasonline.features.supplier.domain.Supplier

@Entity
data class Subscription(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @JsonProperty("diaPagamento")
    val paymentDay: Int,

    @OneToOne
    @JoinColumn(name = "supplier_id", nullable = false)
    @JsonProperty("fornecedor")
    val supplier: Supplier,

    @ManyToOne
    @JoinColumn(name = "plan_id", nullable = false)
    @JsonProperty("plano")
    val plan: Plan,

    @JsonProperty("bannerGrandeUrl")
    val bigBannerUrl: String? = null,

    @JsonProperty("bannerPequenoUrl")
    val smallBannerUrl: String? = null
)
