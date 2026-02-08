package com.syos.web.application.usecases;

import com.syos.web.application.dto.AddressDTO;
import com.syos.web.domain.model.CustomerAddress;
import com.syos.web.infrastructure.persistence.dao.CustomerAddressDao;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Use Case: Manage Customer Addresses
 * Handles all address operations
 */
public class ManageAddressesUseCase {

    private final CustomerAddressDao addressDao;

    public ManageAddressesUseCase() {
        this.addressDao = new CustomerAddressDao();
    }

    /**
     * Get all addresses for user
     */
    public List<AddressDTO> getUserAddresses(String userId) throws SQLException {
        List<CustomerAddress> addresses = addressDao.getUserAddresses(userId);
        List<AddressDTO> dtos = new ArrayList<>();

        for (CustomerAddress address : addresses) {
            dtos.add(convertToDTO(address));
        }

        return dtos;
    }

    /**
     * Add new address
     */
    public AddressDTO addAddress(String userId, AddressDTO dto) throws SQLException {
        dto.validate();

        CustomerAddress address = new CustomerAddress(
                userId,
                dto.getAddressLabel(),
                dto.getFullName(),
                dto.getPhone(),
                dto.getAddressLine1(),
                dto.getAddressLine2(),
                dto.getCity(),
                dto.getPostalCode(),
                dto.isDefault()
        );

        address.validate();

        CustomerAddress saved = addressDao.addAddress(address);
        return convertToDTO(saved);
    }

    /**
     * Update address
     */
    public boolean updateAddress(String userId, Integer addressId, AddressDTO dto) throws SQLException {
        dto.validate();

        CustomerAddress address = new CustomerAddress(
                addressId,
                userId,
                dto.getAddressLabel(),
                dto.getFullName(),
                dto.getPhone(),
                dto.getAddressLine1(),
                dto.getAddressLine2(),
                dto.getCity(),
                dto.getPostalCode(),
                dto.isDefault(),
                null,
                null
        );

        address.validate();

        return addressDao.updateAddress(address);
    }

    /**
     * Delete address
     */
    public boolean deleteAddress(String userId, Integer addressId) throws SQLException {
        return addressDao.deleteAddress(addressId, userId);
    }

    /**
     * Set default address
     */
    public boolean setDefaultAddress(String userId, Integer addressId) throws SQLException {
        return addressDao.setDefaultAddress(addressId, userId);
    }

    /**
     * Convert entity to DTO
     */
    private AddressDTO convertToDTO(CustomerAddress address) {
        AddressDTO dto = new AddressDTO();
        dto.setAddressId(address.getAddressId());
        dto.setAddressLabel(address.getAddressLabel());
        dto.setFullName(address.getFullName());
        dto.setPhone(address.getPhone());
        dto.setAddressLine1(address.getAddressLine1());
        dto.setAddressLine2(address.getAddressLine2());
        dto.setCity(address.getCity());
        dto.setPostalCode(address.getPostalCode());
        dto.setDefault(address.isDefault());
        return dto;
    }
}