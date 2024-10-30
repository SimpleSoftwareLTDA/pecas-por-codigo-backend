package org.pecasonline.features.address.service

import org.pecasonline.features.address.domain.Address
import org.pecasonline.features.address.dto.CreateAddressDTO

interface IAddressService {
    fun save(address: CreateAddressDTO): Address
}