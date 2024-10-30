package org.pecasonline.features.subscription

import jakarta.persistence.*
import org.pecasonline.features.plan.Plan
import org.pecasonline.features.supplier.domain.Supplier

@Entity
data class Subscription(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,
    val paymentDay: Int,

    @OneToOne
    @JoinColumn(name = "supplier_id", nullable = false)
    val supplier: Supplier,

    @ManyToOne
    @JoinColumn(name = "plan_id", nullable = false)
    val plan: Plan,
    val bigBannerUrl: String? = null,
    val smallBannerUrl: String? = null
)
