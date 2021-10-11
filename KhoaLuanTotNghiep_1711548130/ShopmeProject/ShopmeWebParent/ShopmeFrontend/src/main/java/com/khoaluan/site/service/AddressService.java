package com.khoaluan.site.service;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.khoaluan.common.model.Address;
import com.khoaluan.common.model.Customer;
import com.khoaluan.site.repository.AddressRepository;

@Service
@Transactional
public class AddressService {

	@Autowired 
	AddressRepository addressRepository;
	
	public List<Address> listAddressBook(Customer customer) {
		return addressRepository.findByCustomer(customer);
	}

	public void save(Address address) {
		addressRepository.save(address);
	}
	
	public Address get(Integer addressId, Integer customerId) {
		return addressRepository.findByIdAndCustomer(addressId, customerId);
	}
	
	public void delete(Integer addressId, Integer customerId) {
		addressRepository.deleteByIdAndCustomer(addressId, customerId);
	}
	
	public void setDefaultAddress(Integer defaultAddressId, Integer customerId) {
		if (defaultAddressId > 0) {
			addressRepository.setDefaultAddress(defaultAddressId);
		}	
		addressRepository.setNonDefaultForOthers(defaultAddressId, customerId);
	}
	
	public Address getDefaultAddress(Customer customer) {
		return addressRepository.findDefaultByCustomer(customer.getId());
	}
	
}
