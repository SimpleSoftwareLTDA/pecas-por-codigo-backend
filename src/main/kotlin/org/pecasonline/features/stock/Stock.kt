package org.pecasonline.features.stock

import com.fasterxml.jackson.annotation.JsonAlias
import com.fasterxml.jackson.annotation.JsonProperty
import jakarta.persistence.*
import org.pecasonline.features.items.Item
import org.pecasonline.features.supplier.domain.Supplier

@Entity
data class Stock(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @JsonProperty("quantidade")
    @JsonAlias(value = ["quantity"])
    val quantity: Int,

    @ManyToOne
    @JoinColumn(name = "supplier_id", nullable = false)
    @JsonProperty("fornecedor")
    @JsonAlias("supplier")
    val supplier: Supplier? = null,

    @ManyToOne
    @JoinColumn(name = "piece_id", nullable = false)
    @JsonProperty("peca")
    @JsonAlias("piece")
    val item: Item
)
