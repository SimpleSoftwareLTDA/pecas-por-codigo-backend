package org.pecasonline.features.address.service

import org.pecasonline.features.address.domain.Address
import org.pecasonline.features.address.dto.CreateAddressDTO
import org.pecasonline.features.address.dto.UpdateAddressDTO

interface IAddressService {
    fun save(address: CreateAddressDTO): Address
    fun update(existingAddress: Address, updatedAddress: UpdateAddressDTO): Address
}
