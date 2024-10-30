package org.pecasonline.features.address.service

import org.pecasonline.common.exceptions.NotFoundException
import org.pecasonline.features.address.domain.Address
import org.pecasonline.features.address.dto.CreateAddressDTO
import org.pecasonline.features.address.repository.AddressRepository
import org.springframework.stereotype.Service
import java.util.Objects.isNull

@Service
class AddressService(
    private val addressRepository: AddressRepository,
    private val stateService: IStateService
) : IAddressService {

    override fun save(address: CreateAddressDTO): Address {
        val state = stateService.findStateById(address.stateId!!)
        if(isNull(state))
            throw NotFoundException("Invalid state id: ${address.stateId}. No state found in the database")

        return addressRepository.save(
            Address(
                street = address.street!!,
                city = address.city!!,
                cep = address.cep!!,
                country = address.country,
                state = state!!,
            )
        )
    }
}