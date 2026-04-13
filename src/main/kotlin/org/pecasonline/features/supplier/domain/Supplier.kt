package org.pecasonline.features.supplier.domain

import com.fasterxml.jackson.annotation.JsonBackReference
import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonProperty
import jakarta.persistence.*
import org.pecasonline.features.login.entities.Tokens
import org.pecasonline.features.address.domain.Address
import org.pecasonline.features.brand.Brand
import org.pecasonline.features.description.Description
import org.pecasonline.features.subscription.entities.Subscription

@Entity(name = "supplier")
data class Supplier(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Int? = null,

    @JsonProperty("nome")
    val name: String,

    @JsonProperty("linkFornecedorOriginal")
    val supplierOriginalLink: String? = null,

    @JsonProperty("razaoSocial")
    val socialName: String,

    @ManyToOne(cascade = [CascadeType.ALL])
    @JoinColumn(name = "description_id", nullable = true)
    @JsonProperty("descricao")
    val description: Description? = null,

    @ManyToOne(cascade = [CascadeType.ALL])
    @JoinColumn(name = "brand_id", nullable = true)
    @JsonProperty("marca")
    val brand: Brand? = null,

    @Column(unique = true)
    val cnpj: String,

    @JsonProperty("inscricaoEstadual")
    @Column(nullable = true)
    val stateSubscription: String? = null,

    @OneToOne(cascade = [CascadeType.ALL])
    @JoinColumn(name = "address_id", nullable = false)
    @JsonProperty("endereco")
    val address: Address,

    @OneToOne(cascade = [CascadeType.ALL])
    @JoinColumn(name = "contact_id", nullable = false)
    @JsonProperty("contato")
    val contact: Contact,

    @Column(name = "asaas_id", unique = true)
    @JsonIgnore
    val asaasId: String? = null,

    @OneToOne(cascade = [CascadeType.ALL])
    @JoinColumn(name = "token_id", nullable = true)
    @JsonIgnore
    val token: Tokens? = null,

    @OneToOne(mappedBy = "supplier", fetch = FetchType.LAZY)
    @JsonBackReference
    val subscription: Subscription? = null

) {
    override fun toString(): String = "Supplier(id=$id, name=$name, supplierOriginalLink=$supplierOriginalLink, socialName=$socialName, description=$description, brand=$brand, cnpj=$cnpj, stateSubscription=$stateSubscription, address=$address, contact=$contact)"
}