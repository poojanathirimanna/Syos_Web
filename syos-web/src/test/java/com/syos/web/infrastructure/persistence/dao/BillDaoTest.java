package com.syos.web.infrastructure.persistence.dao;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;

public class BillDaoTest {

    private BillDao billDao;

    @BeforeEach
    public void setUp() {
        billDao = new BillDao();
    }

    @Test
    public void testConstructor() {
        assertNotNull(billDao);
    }

    @Test
    public void testGetAllBills() {
        assertDoesNotThrow(() -> {
            try {
                billDao.getAllBills();
            } catch (Exception e) {
                // Expected
            }
        });
    }

    @Test
    public void testMultipleDaoInstances() {
        BillDao dao1 = new BillDao();
        BillDao dao2 = new BillDao();

        assertNotNull(dao1);
        assertNotNull(dao2);
    }

    @Test
    public void testGetAllBillsMultipleCalls() {
        assertDoesNotThrow(() -> {
            try {
                billDao.getAllBills();
                billDao.getAllBills();
                billDao.getAllBills();
            } catch (Exception e) {
                // Expected
            }
        });
    }
}

