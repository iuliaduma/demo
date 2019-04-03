package com.example.demo.repository;

import com.example.demo.data.user.Address;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AddressRepository extends CrudRepository<Address, Long> {

    Address findByCountryAndCityAndStreetAndNumber(String country, String city, String street, String number);
}
