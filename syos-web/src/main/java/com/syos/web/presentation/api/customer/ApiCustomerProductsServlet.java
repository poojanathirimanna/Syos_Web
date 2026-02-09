package com.syos.web.presentation.api.customer;

import com.google.gson.Gson;
import com.syos.web.application.dto.ApiResponse;
import com.syos.web.application.dto.ProductDTO;
import com.syos.web.application.dto.ReviewDTO;
import com.syos.web.domain.model.Product;
import com.syos.web.infrastructure.persistence.dao.ProductDao;
import com.syos.web.infrastructure.persistence.dao.ProductReviewDao;
import com.syos.web.presentation.util.GsonConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Customer Products Servlet
 * Handles product browsing for customers
 *
 * GET /api/customer/products              → Browse all products (with filters)
 * GET /api/customer/products/{code}       → Get product details with reviews
 * GET /api/customer/products/search       → Search products
 */
@WebServlet("/api/customer/products/*")
public class ApiCustomerProductsServlet extends HttpServlet {

    private final Gson gson = GsonConfig.getGson();
    private final ProductDao productDao = new ProductDao();
    private final ProductReviewDao reviewDao = new ProductReviewDao();

    @Override
    public void init() {
        System.out.println("✅ ApiCustomerProductsServlet initialized");
    }

    // GET - Browse products or get product details
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");

        try {
            String pathInfo = req.getPathInfo();

            // Check if requesting single product
            if (pathInfo != null && !pathInfo.equals("/") && !pathInfo.isEmpty()) {
                String productCode = pathInfo.substring(1);
                handleGetProductDetails(productCode, req, resp);
            } else {
                // Browse all products
                handleBrowseProducts(req, resp);
            }

        } catch (Exception e) {
            ApiResponse<Object> error = ApiResponse.error("Failed to retrieve products: " + e.getMessage());
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().write(gson.toJson(error));
            e.printStackTrace();
        }
    }

    /**
     * Handle browsing all products with filters
     */
    private void handleBrowseProducts(HttpServletRequest req, HttpServletResponse resp) throws Exception {
        // Get filter parameters
        String categoryIdStr = req.getParameter("category");
        String search = req.getParameter("search");
        String minPriceStr = req.getParameter("minPrice");
        String maxPriceStr = req.getParameter("maxPrice");
        String sortBy = req.getParameter("sortBy"); // price_asc, price_desc, name_asc, discount_desc
        String pageStr = req.getParameter("page");
        String limitStr = req.getParameter("limit");

        // Parse parameters
        Integer categoryId = categoryIdStr != null ? Integer.parseInt(categoryIdStr) : null;
        BigDecimal minPrice = minPriceStr != null ? new BigDecimal(minPriceStr) : null;
        BigDecimal maxPrice = maxPriceStr != null ? new BigDecimal(maxPriceStr) : null;
        int page = pageStr != null ? Integer.parseInt(pageStr) : 1;
        int limit = limitStr != null ? Integer.parseInt(limitStr) : 20;

        // Get all products
        List<Product> allProducts = productDao.findAll();

        // Filter products
        List<Product> filteredProducts = allProducts.stream()
                .filter(p -> !p.isDeleted()) // Only active products
                .filter(p -> categoryId == null || (p.getCategoryId() != null && p.getCategoryId().equals(categoryId)))
                .filter(p -> search == null || p.getName().toLowerCase().contains(search.toLowerCase()))
                .filter(p -> {
                    // Calculate current price (with discount if any)
                    BigDecimal currentPrice = p.hasActiveDiscount() ? p.getDiscountedPrice() : p.getUnitPrice();
                    return (minPrice == null || currentPrice.compareTo(minPrice) >= 0) &&
                            (maxPrice == null || currentPrice.compareTo(maxPrice) <= 0);
                })
                .collect(Collectors.toList());

        // Sort products
        if (sortBy != null) {
            switch (sortBy) {
                case "price_asc":
                    filteredProducts.sort((p1, p2) -> {
                        BigDecimal price1 = p1.hasActiveDiscount() ? p1.getDiscountedPrice() : p1.getUnitPrice();
                        BigDecimal price2 = p2.hasActiveDiscount() ? p2.getDiscountedPrice() : p2.getUnitPrice();
                        return price1.compareTo(price2);
                    });
                    break;
                case "price_desc":
                    filteredProducts.sort((p1, p2) -> {
                        BigDecimal price1 = p1.hasActiveDiscount() ? p1.getDiscountedPrice() : p1.getUnitPrice();
                        BigDecimal price2 = p2.hasActiveDiscount() ? p2.getDiscountedPrice() : p2.getUnitPrice();
                        return price2.compareTo(price1);
                    });
                    break;
                case "name_asc":
                    filteredProducts.sort((p1, p2) -> p1.getName().compareToIgnoreCase(p2.getName()));
                    break;
                case "discount_desc":
                    filteredProducts.sort((p1, p2) -> {
                        BigDecimal d1 = p1.getDiscountPercentage() != null ? p1.getDiscountPercentage() : BigDecimal.ZERO;
                        BigDecimal d2 = p2.getDiscountPercentage() != null ? p2.getDiscountPercentage() : BigDecimal.ZERO;
                        return d2.compareTo(d1);
                    });
                    break;
            }
        }

        // Pagination
        int totalItems = filteredProducts.size();
        int totalPages = (int) Math.ceil((double) totalItems / limit);
        int startIndex = (page - 1) * limit;
        int endIndex = Math.min(startIndex + limit, totalItems);

        List<Product> paginatedProducts = filteredProducts.subList(
                Math.max(0, startIndex),
                Math.min(totalItems, endIndex)
        );

        // Convert to DTOs with additional info
        List<Map<String, Object>> productDTOs = new ArrayList<>();
        for (Product product : paginatedProducts) {
            Map<String, Object> dto = new HashMap<>();
            dto.put("productCode", product.getProductCode());
            dto.put("name", product.getName());
            dto.put("originalPrice", product.getUnitPrice());

            if (product.hasActiveDiscount()) {
                dto.put("discountedPrice", product.getDiscountedPrice());
                dto.put("discountPercentage", product.getDiscountPercentage());
            } else {
                dto.put("discountedPrice", product.getUnitPrice());
                dto.put("discountPercentage", BigDecimal.ZERO);
            }

            dto.put("imageUrl", product.getImageUrl());
            dto.put("categoryId", product.getCategoryId());

            // Get rating info
            double avgRating = reviewDao.getAverageRating(product.getProductCode());
            int reviewCount = reviewDao.getReviewCount(product.getProductCode());
            dto.put("averageRating", Math.round(avgRating * 10.0) / 10.0); // Round to 1 decimal
            dto.put("reviewCount", reviewCount);

            // Stock info - USE WEBSITE QUANTITY FOR CUSTOMERS!
            boolean inStock = product.getWebsiteQuantity() > 0;
            dto.put("inStock", inStock);
            dto.put("availableQuantity", product.getWebsiteQuantity());

            productDTOs.add(dto);
        }

        // Create response with pagination
        Map<String, Object> responseData = new HashMap<>();
        responseData.put("products", productDTOs);

        Map<String, Object> pagination = new HashMap<>();
        pagination.put("currentPage", page);
        pagination.put("totalPages", totalPages);
        pagination.put("totalItems", totalItems);
        pagination.put("itemsPerPage", limit);
        responseData.put("pagination", pagination);

        ApiResponse<Map<String, Object>> response = ApiResponse.success(
                "Products retrieved successfully",
                responseData
        );

        resp.setStatus(HttpServletResponse.SC_OK);
        resp.getWriter().write(gson.toJson(response));
    }

    /**
     * Handle getting single product details with reviews
     */
    private void handleGetProductDetails(String productCode, HttpServletRequest req, HttpServletResponse resp) throws Exception {
        // Get product
        Product product = productDao.findByCode(productCode)
                .orElseThrow(() -> new IllegalArgumentException("Product not found"));

        if (product.isDeleted()) {
            throw new IllegalArgumentException("Product is no longer available");
        }

        // Get reviews
        List<ReviewDTO> reviews = reviewDao.getProductReviews(productCode);

        // Get rating info
        double avgRating = reviewDao.getAverageRating(productCode);
        int reviewCount = reviewDao.getReviewCount(productCode);

        // Build response
        Map<String, Object> productDetails = new HashMap<>();
        productDetails.put("productCode", product.getProductCode());
        productDetails.put("name", product.getName());
        productDetails.put("description", ""); // Add description field if you have it
        productDetails.put("originalPrice", product.getUnitPrice());

        if (product.hasActiveDiscount()) {
            productDetails.put("discountedPrice", product.getDiscountedPrice());
            productDetails.put("discountPercentage", product.getDiscountPercentage());
            productDetails.put("discountStartDate", product.getDiscountStartDate());
            productDetails.put("discountEndDate", product.getDiscountEndDate());
        } else {
            productDetails.put("discountedPrice", product.getUnitPrice());
            productDetails.put("discountPercentage", BigDecimal.ZERO);
            productDetails.put("discountStartDate", null);
            productDetails.put("discountEndDate", null);
        }

        productDetails.put("imageUrl", product.getImageUrl());
        productDetails.put("categoryId", product.getCategoryId());
        productDetails.put("averageRating", Math.round(avgRating * 10.0) / 10.0);
        productDetails.put("reviewCount", reviewCount);

        // Stock info - USE WEBSITE QUANTITY FOR CUSTOMERS!
        boolean inStock = product.getWebsiteQuantity() > 0;
        productDetails.put("inStock", inStock);
        productDetails.put("availableQuantity", product.getWebsiteQuantity());

        productDetails.put("reviews", reviews);

        ApiResponse<Map<String, Object>> response = ApiResponse.success(
                "Product details retrieved successfully",
                productDetails
        );

        resp.setStatus(HttpServletResponse.SC_OK);
        resp.getWriter().write(gson.toJson(response));
    }

    @Override
    protected void doOptions(HttpServletRequest req, HttpServletResponse resp) {
        resp.setStatus(HttpServletResponse.SC_OK);
    }
}