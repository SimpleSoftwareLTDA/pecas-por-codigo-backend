package org.pecasonline.features.supplier.domain

import jakarta.persistence.*
import org.pecasonline.features.address.domain.Address
import org.pecasonline.features.brand.Brand
import org.pecasonline.features.description.Description
import java.security.MessageDigest

@Entity
class Supplier(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Int? = null,
    val name: String,
    val supplierOriginalLink: String? = null,
    val socialName: String,
    @ManyToOne(cascade = [CascadeType.ALL])
    @JoinColumn(name = "description_id", nullable = true)
    val description: Description? = null,
    @ManyToOne(cascade = [CascadeType.ALL])
    @JoinColumn(name = "brand_id", nullable = true)
    val brand: Brand? = null,
    val cnpj: String,
    val stateSubscription: String,
    @OneToOne(cascade = [CascadeType.ALL])
    @JoinColumn(name = "address_id", nullable = true)
    val address: Address,
    @OneToOne(cascade = [CascadeType.ALL])
    @JoinColumn(name = "contact_id", nullable = true)
    val contact: Contact
) {
    override fun toString(): String {
        return "Supplier(id=$id, name=$name, supplierOriginalLink=$supplierOriginalLink, socialName=$socialName, description=$description, brand=$brand, cnpj=$cnpj, stateSubscription=$stateSubscription, address=$address, contact=$contact)"
    }
}