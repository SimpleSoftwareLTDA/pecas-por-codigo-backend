package org.pecasonline.features.category

import com.fasterxml.jackson.annotation.JsonAlias
import com.fasterxml.jackson.annotation.JsonProperty
import jakarta.persistence.*

@Entity
data class Category(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "category_id")
    val id: Int? = null,

    @JsonProperty("nome")
    @JsonAlias(value = ["name"])
    val name: String
)