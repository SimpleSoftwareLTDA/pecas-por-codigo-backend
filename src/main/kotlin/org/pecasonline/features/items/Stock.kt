package org.pecasonline.features.items

import jakarta.persistence.*
import org.pecasonline.features.supplier.domain.Supplier

@Entity
data class Stock(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    val quantity: Int,

    @ManyToOne
    @JoinColumn(name = "supplier_id", nullable = false)
    val supplier: Supplier,

    @ManyToOne
    @JoinColumn(name = "item_id", nullable = false)
    val item: Item
)
