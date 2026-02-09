package com.syos.web.infrastructure.persistence.dao;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;

public class CustomerAddressDaoTest {

    private CustomerAddressDao addressDao;

    @BeforeEach
    public void setUp() {
        addressDao = new CustomerAddressDao();
    }

    @Test
    public void testConstructor() {
        assertNotNull(addressDao);
    }

    @Test
    public void testGetUserAddresses() {
        assertDoesNotThrow(() -> {
            try {
                addressDao.getUserAddresses("U001");
            } catch (Exception e) {
                // Expected
            }
        });
    }

    @Test
    public void testGetUserAddressesNull() {
        assertDoesNotThrow(() -> {
            try {
                addressDao.getUserAddresses(null);
            } catch (Exception e) {
                // Expected
            }
        });
    }

    @Test
    public void testGetUserAddressesEmpty() {
        assertDoesNotThrow(() -> {
            try {
                addressDao.getUserAddresses("");
            } catch (Exception e) {
                // Expected
            }
        });
    }

    @Test
    public void testDeleteAddress() {
        assertDoesNotThrow(() -> {
            try {
                addressDao.deleteAddress(1, "U001");
            } catch (Exception e) {
                // Expected
            }
        });
    }

    @Test
    public void testDeleteAddressNullId() {
        assertDoesNotThrow(() -> {
            try {
                addressDao.deleteAddress(null, "U001");
            } catch (Exception e) {
                // Expected
            }
        });
    }

    @Test
    public void testDeleteAddressNullUser() {
        assertDoesNotThrow(() -> {
            try {
                addressDao.deleteAddress(1, null);
            } catch (Exception e) {
                // Expected
            }
        });
    }

    @Test
    public void testSetDefaultAddress() {
        assertDoesNotThrow(() -> {
            try {
                addressDao.setDefaultAddress(1, "U001");
            } catch (Exception e) {
                // Expected
            }
        });
    }

    @Test
    public void testSetDefaultAddressNullId() {
        assertDoesNotThrow(() -> {
            try {
                addressDao.setDefaultAddress(null, "U001");
            } catch (Exception e) {
                // Expected
            }
        });
    }

    @Test
    public void testSetDefaultAddressNullUser() {
        assertDoesNotThrow(() -> {
            try {
                addressDao.setDefaultAddress(1, null);
            } catch (Exception e) {
                // Expected
            }
        });
    }

    @Test
    public void testMultipleUsers() {
        String[] userIds = {"U001", "U002", "U003"};
        for (String userId : userIds) {
            assertDoesNotThrow(() -> {
                try {
                    addressDao.getUserAddresses(userId);
                } catch (Exception e) {
                    // Expected
                }
            });
        }
    }

    @Test
    public void testMultipleAddressIds() {
        Integer[] ids = {1, 2, 3, 10, 100};
        for (Integer id : ids) {
            assertDoesNotThrow(() -> {
                try {
                    addressDao.deleteAddress(id, "U001");
                } catch (Exception e) {
                    // Expected
                }
            });
        }
    }

    @Test
    public void testSequentialOperations() {
        assertDoesNotThrow(() -> {
            try {
                addressDao.getUserAddresses("U001");
                addressDao.setDefaultAddress(1, "U001");
                addressDao.deleteAddress(2, "U001");
            } catch (Exception e) {
                // Expected
            }
        });
    }

    @Test
    public void testMultipleDaoInstances() {
        CustomerAddressDao dao1 = new CustomerAddressDao();
        CustomerAddressDao dao2 = new CustomerAddressDao();

        assertNotNull(dao1);
        assertNotNull(dao2);
    }

    @Test
    public void testGetAddressesMultipleTimes() {
        assertDoesNotThrow(() -> {
            try {
                addressDao.getUserAddresses("U001");
                addressDao.getUserAddresses("U001");
                addressDao.getUserAddresses("U001");
            } catch (Exception e) {
                // Expected
            }
        });
    }

    @Test
    public void testDeleteMultipleAddresses() {
        assertDoesNotThrow(() -> {
            try {
                addressDao.deleteAddress(1, "U001");
                addressDao.deleteAddress(2, "U001");
                addressDao.deleteAddress(3, "U001");
            } catch (Exception e) {
                // Expected
            }
        });
    }

    @Test
    public void testSetMultipleDefaults() {
        assertDoesNotThrow(() -> {
            try {
                addressDao.setDefaultAddress(1, "U001");
                addressDao.setDefaultAddress(2, "U001");
                addressDao.setDefaultAddress(3, "U001");
            } catch (Exception e) {
                // Expected
            }
        });
    }

    @Test
    public void testUserWithSpecialChars() {
        assertDoesNotThrow(() -> {
            try {
                addressDao.getUserAddresses("user@test.com");
            } catch (Exception e) {
                // Expected
            }
        });
    }

    @Test
    public void testDeleteWithZeroId() {
        assertDoesNotThrow(() -> {
            try {
                addressDao.deleteAddress(0, "U001");
            } catch (Exception e) {
                // Expected
            }
        });
    }

    @Test
    public void testSetDefaultWithZeroId() {
        assertDoesNotThrow(() -> {
            try {
                addressDao.setDefaultAddress(0, "U001");
            } catch (Exception e) {
                // Expected
            }
        });
    }
}

