package org.pecasonline.features.stock

import com.fasterxml.jackson.annotation.JsonAlias
import com.fasterxml.jackson.annotation.JsonProperty
import jakarta.persistence.*
import org.pecasonline.features.items.Item
import org.pecasonline.features.supplier.domain.Supplier

@Entity
@Table(name = "stock", indexes = [Index(name = "idx_stock_supplier_id", columnList = "supplier_id")])
data class Stock(
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "stock_seq_gen")
    @SequenceGenerator(name = "stock_seq_gen", sequenceName = "stock_seq", allocationSize = 1)
    val id: Long? = null,

    @JsonProperty("quantidade")
    @JsonAlias(value = ["quantity"])
    val quantity: Int,

    @ManyToOne(optional = false)
    @JoinColumn(name = "supplier_id", nullable = false)
    @JsonProperty("fornecedor")
    @JsonAlias("supplier")
    val supplier: Supplier? = null,

    @ManyToOne(optional = false)
    @JoinColumn(name = "piece_id", nullable = false)
    @JsonProperty("peca")
    @JsonAlias("piece")
    val item: Item
)
