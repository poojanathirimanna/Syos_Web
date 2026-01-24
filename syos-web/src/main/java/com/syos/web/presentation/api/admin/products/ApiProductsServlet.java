package com.syos.web.presentation.api.admin.products;

import com.google.gson.Gson;
import com.syos.web.application.dto.*;
import com.syos.web.application.ports.IProductRepository;
import com.syos.web.application.usecases.*;
import com.syos.web.concurrency.RequestLogger;
import com.syos.web.infrastructure.repositories.ProductRepositoryImpl;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.io.BufferedReader;

/**
 * Complete Products Servlet - Handles all product operations
 * GET    /api/admin/products          → List all products (catalog)
 * GET    /api/admin/products/{code}   → Get single product details
 * POST   /api/admin/products          → Create new product
 * PUT    /api/admin/products          → Update existing product
 * DELETE /api/admin/products/{code}   → Delete product
 *
 * Clean Architecture Flow:
 * Servlet → Use Case → Repository → DAO → Database
 */
@WebServlet("/api/admin/products/*")
public class ApiProductsServlet extends HttpServlet {

    private final Gson gson = new Gson();

    // Use Cases
    private ProductCatalogUseCase productCatalogUseCase;
    private CreateProductUseCase createProductUseCase;
    private UpdateProductUseCase updateProductUseCase;
    private DeleteProductUseCase deleteProductUseCase;
    private GetProductDetailsUseCase getProductDetailsUseCase;

    @Override
    public void init() throws ServletException {
        // Initialize repository (Dependency Injection)
        IProductRepository productRepository = new ProductRepositoryImpl();

        // Initialize use cases
        this.productCatalogUseCase = new ProductCatalogUseCase(productRepository);
        this.createProductUseCase = new CreateProductUseCase(productRepository);
        this.updateProductUseCase = new UpdateProductUseCase(productRepository);
        this.deleteProductUseCase = new DeleteProductUseCase(productRepository);
        this.getProductDetailsUseCase = new GetProductDetailsUseCase(productRepository);

        System.out.println("✅ ProductsServlet initialized with all CRUD operations");
    }

    // ===================================================================
    // GET - List all products OR get single product
    // ===================================================================
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        long startTime = System.currentTimeMillis();
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");

        HttpSession session = req.getSession(false);
        String userId = (session != null) ? (String) session.getAttribute("username") : "anonymous";

        String pathInfo = req.getPathInfo(); // null, "", "/" or "/{productCode}"

        try {
            // Determine if catalog or single product
            if (pathInfo == null || pathInfo.equals("/") || pathInfo.equals("")) {
                // GET /api/admin/products → List all
                handleCatalog(req, resp, userId, startTime);
            } else {
                // GET /api/admin/products/{code} → Get one
                String productCode = extractProductCode(pathInfo);
                handleGetOne(req, resp, userId, productCode, startTime);
            }
        } catch (Exception e) {
            handleError(resp, userId, "GET_PRODUCT", e, startTime);
        }
    }

    // ===================================================================
    // POST - Create new product
    // ===================================================================
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        long startTime = System.currentTimeMillis();
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");

        HttpSession session = req.getSession(false);
        String userId = (session != null) ? (String) session.getAttribute("username") : "anonymous";

        String requestId = RequestLogger.logRequest(
                "CREATE_PRODUCT",
                userId,
                req.getRemoteAddr(),
                Thread.currentThread().getName()
        );

        try {
            // Read request body
            String requestBody = readRequestBody(req);
            CreateProductRequest request = gson.fromJson(requestBody, CreateProductRequest.class);

            // Execute use case
            ProductDTO productDTO = createProductUseCase.execute(request);

            // Success response
            ApiResponse response = ApiResponse.success(
                    "Product created successfully",
                    productDTO
            );

            RequestLogger.updateStatus(requestId, "COMPLETED", startTime);
            resp.setStatus(HttpServletResponse.SC_CREATED); // 201
            resp.getWriter().write(gson.toJson(response));

        } catch (IllegalArgumentException e) {
            // Validation error (400)
            RequestLogger.logError(requestId, e.getMessage());
            ApiResponse errorResponse = ApiResponse.error(e.getMessage());
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write(gson.toJson(errorResponse));

        } catch (Exception e) {
            RequestLogger.logError(requestId, "Failed to create product: " + e.getMessage());
            ApiResponse errorResponse = ApiResponse.error("Failed to create product: " + e.getMessage());
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().write(gson.toJson(errorResponse));
            e.printStackTrace();
        }
    }

    // ===================================================================
    // PUT - Update existing product
    // ===================================================================
    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        long startTime = System.currentTimeMillis();
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");

        HttpSession session = req.getSession(false);
        String userId = (session != null) ? (String) session.getAttribute("username") : "anonymous";

        String requestId = RequestLogger.logRequest(
                "UPDATE_PRODUCT",
                userId,
                req.getRemoteAddr(),
                Thread.currentThread().getName()
        );

        try {
            // Read request body
            String requestBody = readRequestBody(req);
            UpdateProductRequest request = gson.fromJson(requestBody, UpdateProductRequest.class);

            // Execute use case
            ProductDTO productDTO = updateProductUseCase.execute(request);

            // Success response
            ApiResponse response = ApiResponse.success(
                    "Product updated successfully",
                    productDTO
            );

            RequestLogger.updateStatus(requestId, "COMPLETED", startTime);
            resp.setStatus(HttpServletResponse.SC_OK);
            resp.getWriter().write(gson.toJson(response));

        } catch (IllegalArgumentException e) {
            RequestLogger.logError(requestId, e.getMessage());
            ApiResponse errorResponse = ApiResponse.error(e.getMessage());
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write(gson.toJson(errorResponse));

        } catch (Exception e) {
            RequestLogger.logError(requestId, "Failed to update product: " + e.getMessage());
            ApiResponse errorResponse = ApiResponse.error("Failed to update product: " + e.getMessage());
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().write(gson.toJson(errorResponse));
            e.printStackTrace();
        }
    }

    // ===================================================================
    // DELETE - Delete product
    // ===================================================================
    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        long startTime = System.currentTimeMillis();
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");

        HttpSession session = req.getSession(false);
        String userId = (session != null) ? (String) session.getAttribute("username") : "anonymous";

        String requestId = RequestLogger.logRequest(
                "DELETE_PRODUCT",
                userId,
                req.getRemoteAddr(),
                Thread.currentThread().getName()
        );

        try {
            String pathInfo = req.getPathInfo();
            String productCode = extractProductCode(pathInfo);

            if (productCode == null || productCode.trim().isEmpty()) {
                throw new IllegalArgumentException("Product code is required");
            }

            // Execute use case
            boolean deleted = deleteProductUseCase.execute(productCode);

            if (deleted) {
                ApiResponse response = ApiResponse.success("Product deleted successfully", null);
                RequestLogger.updateStatus(requestId, "COMPLETED", startTime);
                resp.setStatus(HttpServletResponse.SC_OK);
                resp.getWriter().write(gson.toJson(response));
            } else {
                throw new IllegalArgumentException("Product not found: " + productCode);
            }

        } catch (IllegalArgumentException e) {
            RequestLogger.logError(requestId, e.getMessage());
            ApiResponse errorResponse = ApiResponse.error(e.getMessage());
            resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
            resp.getWriter().write(gson.toJson(errorResponse));

        } catch (Exception e) {
            RequestLogger.logError(requestId, "Failed to delete product: " + e.getMessage());
            ApiResponse errorResponse = ApiResponse.error("Failed to delete product: " + e.getMessage());
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().write(gson.toJson(errorResponse));
            e.printStackTrace();
        }
    }

    // ===================================================================
    // HELPER METHODS
    // ===================================================================

    /**
     * Handle catalog listing
     */
    private void handleCatalog(HttpServletRequest req, HttpServletResponse resp, String userId, long startTime) throws IOException {
        String requestId = RequestLogger.logRequest(
                "VIEW_PRODUCT_CATALOG",
                userId,
                req.getRemoteAddr(),
                Thread.currentThread().getName()
        );

        try {
            ProductCatalogDTO catalogDTO = productCatalogUseCase.execute();

            ApiResponse response = ApiResponse.success(
                    "Product catalog retrieved successfully",
                    catalogDTO
            );

            RequestLogger.updateStatus(requestId, "COMPLETED", startTime);
            resp.setStatus(HttpServletResponse.SC_OK);
            resp.getWriter().write(gson.toJson(response));

        } catch (Exception e) {
            RequestLogger.logError(requestId, "Failed to retrieve catalog: " + e.getMessage());
            ApiResponse errorResponse = ApiResponse.error("Failed to retrieve product catalog: " + e.getMessage());
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().write(gson.toJson(errorResponse));
            e.printStackTrace();
        }
    }

    /**
     * Handle single product details
     */
    private void handleGetOne(HttpServletRequest req, HttpServletResponse resp, String userId, String productCode, long startTime) throws IOException {
        String requestId = RequestLogger.logRequest(
                "GET_PRODUCT_DETAILS",
                userId,
                req.getRemoteAddr(),
                Thread.currentThread().getName()
        );

        try {
            ProductDTO productDTO = getProductDetailsUseCase.execute(productCode);

            ApiResponse response = ApiResponse.success(
                    "Product details retrieved successfully",
                    productDTO
            );

            RequestLogger.updateStatus(requestId, "COMPLETED", startTime);
            resp.setStatus(HttpServletResponse.SC_OK);
            resp.getWriter().write(gson.toJson(response));

        } catch (IllegalArgumentException e) {
            RequestLogger.logError(requestId, e.getMessage());
            ApiResponse errorResponse = ApiResponse.error(e.getMessage());
            resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
            resp.getWriter().write(gson.toJson(errorResponse));

        } catch (Exception e) {
            RequestLogger.logError(requestId, "Failed to get product: " + e.getMessage());
            ApiResponse errorResponse = ApiResponse.error("Failed to retrieve product: " + e.getMessage());
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().write(gson.toJson(errorResponse));
            e.printStackTrace();
        }
    }

    /**
     * Extract product code from path
     */
    private String extractProductCode(String pathInfo) {
        if (pathInfo != null && pathInfo.length() > 1) {
            return pathInfo.substring(1); // Remove leading "/"
        }
        return null;
    }

    /**
     * Read request body as string
     */
    private String readRequestBody(HttpServletRequest req) throws IOException {
        StringBuilder sb = new StringBuilder();
        BufferedReader reader = req.getReader();
        String line;
        while ((line = reader.readLine()) != null) {
            sb.append(line);
        }
        return sb.toString();
    }

    /**
     * Handle errors
     */
    private void handleError(HttpServletResponse resp, String userId, String action, Exception e, long startTime) throws IOException {
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");

        ApiResponse errorResponse = ApiResponse.error("Error: " + e.getMessage());
        resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        resp.getWriter().write(gson.toJson(errorResponse));
        e.printStackTrace();
    }

    @Override
    protected void doOptions(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setStatus(HttpServletResponse.SC_OK);
    }
}