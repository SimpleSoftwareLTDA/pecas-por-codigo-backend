package org.pecasonline.features.address.domain

import jakarta.persistence.*

@Entity
data class Address(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Int? = null,
    val street: String,
    val city: String,
    @ManyToOne(cascade = [CascadeType.MERGE])
    @JoinColumn(name = "state_id", nullable = false)
    val state: BrazilianState,
    val cep: String,
    val country: String? = "Brasil",
)
