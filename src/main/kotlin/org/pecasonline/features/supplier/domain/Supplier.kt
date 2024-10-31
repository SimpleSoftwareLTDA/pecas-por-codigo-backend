package org.pecasonline.features.supplier.domain

import com.fasterxml.jackson.annotation.JsonProperty
import jakarta.persistence.*
import org.pecasonline.features.address.domain.Address
import org.pecasonline.features.brand.Brand
import org.pecasonline.features.description.Description
import java.security.MessageDigest

@Entity
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

    val cnpj: String,

    @JsonProperty("inscricaoEstadual")
    val stateSubscription: String,

    @OneToOne(cascade = [CascadeType.ALL])
    @JoinColumn(name = "address_id", nullable = true)
    @JsonProperty("endereco")
    val address: Address,

    @OneToOne(cascade = [CascadeType.ALL])
    @JoinColumn(name = "contact_id", nullable = true)
    @JsonProperty("contato")
    val contact: Contact
) {
    override fun toString(): String {
        return "Supplier(id=$id, name=$name, supplierOriginalLink=$supplierOriginalLink, socialName=$socialName, description=$description, brand=$brand, cnpj=$cnpj, stateSubscription=$stateSubscription, address=$address, contact=$contact)"
    }
}