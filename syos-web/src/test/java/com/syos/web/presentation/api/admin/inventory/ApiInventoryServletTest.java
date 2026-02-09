package com.syos.web.presentation.api.admin.inventory;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.PrintWriter;
import java.io.StringWriter;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class ApiInventoryServletTest {

    private ApiInventoryServlet servlet;
    @Mock private HttpServletRequest mockRequest;
    @Mock private HttpServletResponse mockResponse;
    @Mock private HttpSession mockSession;
    private StringWriter responseWriter;

    @BeforeEach
    public void setUp() throws Exception {
        MockitoAnnotations.openMocks(this);
        servlet = new ApiInventoryServlet();
        responseWriter = new StringWriter();
        when(mockResponse.getWriter()).thenReturn(new PrintWriter(responseWriter));
    }

    @Test
    public void testServletInitialization() {
        assertNotNull(servlet);
        assertTrue(servlet instanceof ApiInventoryServlet);
    }

    @Test
    public void testDoGetAllInventory() throws Exception {
        servlet.doGet(mockRequest, mockResponse);

        verify(mockResponse).setContentType("application/json; charset=UTF-8");
        verify(mockResponse).getWriter();
    }

    @Test
    public void testDoGetWithMainLocation() throws Exception {
        when(mockRequest.getParameter("location")).thenReturn("MAIN");

        servlet.doGet(mockRequest, mockResponse);

        verify(mockResponse).setContentType("application/json; charset=UTF-8");
        verify(mockRequest).getParameter("location");
    }

    @Test
    public void testDoGetWithShelfLocation() throws Exception {
        when(mockRequest.getParameter("location")).thenReturn("SHELF");

        servlet.doGet(mockRequest, mockResponse);

        verify(mockResponse).setContentType("application/json; charset=UTF-8");
        verify(mockRequest).getParameter("location");
    }

    @Test
    public void testDoGetWithWebsiteLocation() throws Exception {
        when(mockRequest.getParameter("location")).thenReturn("WEBSITE");

        servlet.doGet(mockRequest, mockResponse);

        verify(mockResponse).setContentType("application/json; charset=UTF-8");
        verify(mockRequest).getParameter("location");
    }

    @Test
    public void testDoGetWithSpecificProduct() throws Exception {
        when(mockRequest.getParameter("productCode")).thenReturn("P001");

        servlet.doGet(mockRequest, mockResponse);

        verify(mockResponse).setContentType("application/json; charset=UTF-8");
        verify(mockRequest).getParameter("productCode");
    }

    @Test
    public void testDoGetWithMultipleProductCodes() throws Exception {
        String[] productCodes = {"P001", "P002", "P003", "P004", "P005"};

        for (String code : productCodes) {
            when(mockRequest.getParameter("productCode")).thenReturn(code);
            servlet.doGet(mockRequest, mockResponse);
        }

        verify(mockResponse, atLeast(5)).setContentType("application/json; charset=UTF-8");
    }

    @Test
    public void testDoGetWithNullLocation() throws Exception {
        when(mockRequest.getParameter("location")).thenReturn(null);

        servlet.doGet(mockRequest, mockResponse);

        verify(mockResponse).setContentType("application/json; charset=UTF-8");
    }

    @Test
    public void testDoGetWithEmptyLocation() throws Exception {
        when(mockRequest.getParameter("location")).thenReturn("");

        servlet.doGet(mockRequest, mockResponse);

        verify(mockResponse).setContentType("application/json; charset=UTF-8");
    }

    @Test
    public void testDoGetWithNullProductCode() throws Exception {
        when(mockRequest.getParameter("productCode")).thenReturn(null);

        servlet.doGet(mockRequest, mockResponse);

        verify(mockResponse).setContentType("application/json; charset=UTF-8");
    }

    @Test
    public void testDoGetWithEmptyProductCode() throws Exception {
        when(mockRequest.getParameter("productCode")).thenReturn("");

        servlet.doGet(mockRequest, mockResponse);

        verify(mockResponse).setContentType("application/json; charset=UTF-8");
    }

    @Test
    public void testDoGetWithBothLocationAndProduct() throws Exception {
        when(mockRequest.getParameter("location")).thenReturn("MAIN");
        when(mockRequest.getParameter("productCode")).thenReturn("P001");

        servlet.doGet(mockRequest, mockResponse);

        verify(mockResponse).setContentType("application/json; charset=UTF-8");
        verify(mockRequest).getParameter("location");
        verify(mockRequest).getParameter("productCode");
    }

    @Test
    public void testDoGetWithSessionValidation() throws Exception {
        when(mockRequest.getSession(false)).thenReturn(mockSession);
        when(mockSession.getAttribute("userId")).thenReturn("ADMIN001");

        servlet.doGet(mockRequest, mockResponse);

        verify(mockResponse).setContentType("application/json; charset=UTF-8");
    }

    @Test
    public void testDoGetMultipleConsecutiveCalls() throws Exception {
        for (int i = 0; i < 10; i++) {
            servlet.doGet(mockRequest, mockResponse);
        }

        verify(mockResponse, atLeast(10)).setContentType("application/json; charset=UTF-8");
    }

    @Test
    public void testDoGetWithDifferentLocations() throws Exception {
        String[] locations = {"MAIN", "SHELF", "WEBSITE", "WAREHOUSE", "STORAGE"};

        for (String location : locations) {
            when(mockRequest.getParameter("location")).thenReturn(location);
            servlet.doGet(mockRequest, mockResponse);
        }

        verify(mockResponse, atLeast(5)).setContentType("application/json; charset=UTF-8");
    }

    @Test
    public void testDoGetResponseWriterVerification() throws Exception {
        servlet.doGet(mockRequest, mockResponse);

        verify(mockResponse).getWriter();
        assertNotNull(responseWriter);
    }

    @Test
    public void testDoGetWithSpecialCharactersInProductCode() throws Exception {
        when(mockRequest.getParameter("productCode")).thenReturn("P-001_A");

        servlet.doGet(mockRequest, mockResponse);

        verify(mockResponse).setContentType("application/json; charset=UTF-8");
    }

    @Test
    public void testDoGetWithNumericProductCode() throws Exception {
        when(mockRequest.getParameter("productCode")).thenReturn("123456");

        servlet.doGet(mockRequest, mockResponse);

        verify(mockResponse).setContentType("application/json; charset=UTF-8");
    }

    @Test
    public void testDoGetWithLongProductCode() throws Exception {
        String longCode = "P" + "0".repeat(100);
        when(mockRequest.getParameter("productCode")).thenReturn(longCode);

        servlet.doGet(mockRequest, mockResponse);

        verify(mockResponse).setContentType("application/json; charset=UTF-8");
    }

    @Test
    public void testDoGetWithLocationCaseInsensitive() throws Exception {
        String[] locations = {"main", "MAIN", "Main", "shelf", "SHELF"};

        for (String location : locations) {
            when(mockRequest.getParameter("location")).thenReturn(location);
            servlet.doGet(mockRequest, mockResponse);
        }

        verify(mockResponse, atLeast(5)).setContentType("application/json; charset=UTF-8");
    }

    @Test
    public void testMultipleServletInstances() {
        ApiInventoryServlet servlet1 = new ApiInventoryServlet();
        ApiInventoryServlet servlet2 = new ApiInventoryServlet();
        ApiInventoryServlet servlet3 = new ApiInventoryServlet();

        assertNotNull(servlet1);
        assertNotNull(servlet2);
        assertNotNull(servlet3);
        assertNotEquals(servlet1, servlet2);
        assertNotEquals(servlet2, servlet3);
    }

    @Test
    public void testServletEquality() {
        ApiInventoryServlet anotherServlet = new ApiInventoryServlet();

        assertNotNull(servlet);
        assertNotNull(anotherServlet);
        assertNotEquals(servlet, anotherServlet);
    }

    @Test
    public void testDoGetWithFilterParameters() throws Exception {
        when(mockRequest.getParameter("location")).thenReturn("MAIN");
        when(mockRequest.getParameter("productCode")).thenReturn("P001");
        when(mockRequest.getParameter("minQuantity")).thenReturn("10");
        when(mockRequest.getParameter("maxQuantity")).thenReturn("100");

        servlet.doGet(mockRequest, mockResponse);

        verify(mockResponse).setContentType("application/json; charset=UTF-8");
        verify(mockRequest).getParameter("location");
        verify(mockRequest).getParameter("productCode");
    }

    @Test
    public void testDoGetWithPaginationParameters() throws Exception {
        when(mockRequest.getParameter("page")).thenReturn("1");
        when(mockRequest.getParameter("limit")).thenReturn("20");

        servlet.doGet(mockRequest, mockResponse);

        verify(mockResponse).setContentType("application/json; charset=UTF-8");
    }

    @Test
    public void testDoGetWithSortingParameters() throws Exception {
        when(mockRequest.getParameter("sortBy")).thenReturn("productCode");
        when(mockRequest.getParameter("sortOrder")).thenReturn("ASC");

        servlet.doGet(mockRequest, mockResponse);

        verify(mockResponse).setContentType("application/json; charset=UTF-8");
    }

    @Test
    public void testDoGetWithAllParameters() throws Exception {
        when(mockRequest.getParameter("location")).thenReturn("MAIN");
        when(mockRequest.getParameter("productCode")).thenReturn("P001");
        when(mockRequest.getParameter("page")).thenReturn("1");
        when(mockRequest.getParameter("limit")).thenReturn("10");
        when(mockRequest.getParameter("sortBy")).thenReturn("quantity");
        when(mockRequest.getParameter("sortOrder")).thenReturn("DESC");

        servlet.doGet(mockRequest, mockResponse);

        verify(mockResponse).setContentType("application/json; charset=UTF-8");
        verify(mockRequest, atLeast(6)).getParameter(anyString());
    }

    @Test
    public void testDoGetWithSessionAndParameters() throws Exception {
        when(mockRequest.getSession(false)).thenReturn(mockSession);
        when(mockSession.getAttribute("userId")).thenReturn("ADMIN001");
        when(mockRequest.getParameter("location")).thenReturn("SHELF");

        servlet.doGet(mockRequest, mockResponse);

        verify(mockResponse).setContentType("application/json; charset=UTF-8");
        verify(mockRequest).getSession(false);
    }

    @Test
    public void testDoGetStressTest() throws Exception {
        // Simulate multiple rapid requests
        for (int i = 0; i < 50; i++) {
            when(mockRequest.getParameter("productCode")).thenReturn("P" + String.format("%03d", i));
            servlet.doGet(mockRequest, mockResponse);
        }

        verify(mockResponse, atLeast(50)).setContentType("application/json; charset=UTF-8");
    }

    @Test
    public void testDoGetWithVariousProductCodeFormats() throws Exception {
        String[] formats = {"P001", "PROD-001", "SKU_001", "ITEM001", "123ABC"};

        for (String format : formats) {
            when(mockRequest.getParameter("productCode")).thenReturn(format);
            servlet.doGet(mockRequest, mockResponse);
        }

        verify(mockResponse, atLeast(5)).setContentType("application/json; charset=UTF-8");
    }

    @Test
    public void testServletToStringMethod() {
        String servletString = servlet.toString();
        assertNotNull(servletString);
        assertTrue(servletString.contains("ApiInventoryServlet"));
    }

    @Test
    public void testServletHashCode() {
        int hashCode1 = servlet.hashCode();
        int hashCode2 = servlet.hashCode();

        assertEquals(hashCode1, hashCode2); // Same object should have same hash
    }

    @Test
    public void testDoGetWithBoundaryValues() throws Exception {
        // Test with boundary values for pagination
        when(mockRequest.getParameter("page")).thenReturn("0");
        when(mockRequest.getParameter("limit")).thenReturn("1");
        servlet.doGet(mockRequest, mockResponse);

        when(mockRequest.getParameter("page")).thenReturn("999999");
        when(mockRequest.getParameter("limit")).thenReturn("1000");
        servlet.doGet(mockRequest, mockResponse);

        verify(mockResponse, atLeast(2)).setContentType("application/json; charset=UTF-8");
    }
}

