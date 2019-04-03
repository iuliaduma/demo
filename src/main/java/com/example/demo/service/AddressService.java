package com.example.demo.service;

import com.example.demo.data.user.Address;
import com.example.demo.dto.user.AddressDTO;
import com.example.demo.mapping.GenericModelMapper;
import com.example.demo.repository.AddressRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class AddressService {

    private final AddressRepository addressRepository;

    private final GenericModelMapper addressMapper;

    public AddressDTO findByCountryAndCityAndStreetAndNumber(AddressDTO address){
        return addressMapper.map(
                addressRepository.findByCountryAndCityAndStreetAndNumber(
                        address.getCountry(), address.getCity(), address.getStreet(), address.getNumber()), AddressDTO.class);
    }

    public AddressDTO saveOrUpdate(AddressDTO addressDTO){

        Address address = addressRepository.findByCountryAndCityAndStreetAndNumber(
                addressDTO.getCountry(), addressDTO.getCity(), addressDTO.getStreet(), addressDTO.getNumber());

        if (address != null){
            addressDTO.setId(address.getId());
        }

        return addressMapper.map(addressRepository.save(addressMapper.map(addressDTO, Address.class)), AddressDTO.class);
    }
}
