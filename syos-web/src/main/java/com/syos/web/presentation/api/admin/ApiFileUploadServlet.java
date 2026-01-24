package com.syos.web.presentation.api.admin;

import com.google.gson.Gson;
import com.syos.web.application.dto.ApiResponse;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Part;

import java.io.File;
import java.io.IOException;

/**
 * File Upload Servlet - Handles image uploads
 * POST /api/admin/upload
 */
@WebServlet("/api/admin/upload")
@MultipartConfig(
        fileSizeThreshold = 1024 * 1024,      // 1MB
        maxFileSize = 1024 * 1024 * 5,        // 5MB max file size
        maxRequestSize = 1024 * 1024 * 10     // 10MB max request size
)
public class ApiFileUploadServlet extends HttpServlet {

    private final Gson gson = new Gson();
    private static final String UPLOAD_DIR = "uploads" + File.separator + "products";

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");

        try {
            // Get the uploaded file part
            Part filePart = req.getPart("image");

            if (filePart == null) {
                ApiResponse<Object> error = ApiResponse.error("No file uploaded");
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                resp.getWriter().write(gson.toJson(error));
                return;
            }

            // Get original filename
            String fileName = getFileName(filePart);

            if (fileName == null || fileName.isEmpty()) {
                ApiResponse<Object> error = ApiResponse.error("Invalid file");
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                resp.getWriter().write(gson.toJson(error));
                return;
            }

            // Validate file type
            if (!isValidImageFile(fileName)) {
                ApiResponse<Object> error = ApiResponse.error("Only image files allowed (jpg, jpeg, png, gif)");
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                resp.getWriter().write(gson.toJson(error));
                return;
            }

            // Generate unique filename
            String uniqueFileName = generateUniqueFileName(fileName);

            // Get upload directory path (relative to webapp root)
            String uploadPath = getServletContext().getRealPath("") + File.separator + UPLOAD_DIR;

            // Create directory if it doesn't exist
            File uploadDir = new File(uploadPath);
            if (!uploadDir.exists()) {
                uploadDir.mkdirs();
            }

            // Save file to disk
            String filePath = uploadPath + File.separator + uniqueFileName;
            filePart.write(filePath);

            // Return relative path (to store in database and use in frontend)
            String relativePath = "/" + UPLOAD_DIR.replace(File.separator, "/") + "/" + uniqueFileName;

            ApiResponse<String> response = ApiResponse.success(
                    "File uploaded successfully",
                    relativePath
            );

            resp.setStatus(HttpServletResponse.SC_OK);
            resp.getWriter().write(gson.toJson(response));

        } catch (Exception e) {
            ApiResponse<Object> error = ApiResponse.error("Failed to upload file: " + e.getMessage());
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().write(gson.toJson(error));
            e.printStackTrace();
        }
    }

    /**
     * Extract filename from Part header
     */
    private String getFileName(Part part) {
        String contentDisposition = part.getHeader("content-disposition");
        if (contentDisposition == null) {
            return null;
        }

        for (String content : contentDisposition.split(";")) {
            if (content.trim().startsWith("filename")) {
                return content.substring(content.indexOf('=') + 1)
                        .trim()
                        .replace("\"", "");
            }
        }
        return null;
    }

    /**
     * Validate if file is an image
     */
    private boolean isValidImageFile(String fileName) {
        if (fileName == null || !fileName.contains(".")) {
            return false;
        }

        String extension = fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase();
        return extension.equals("jpg") ||
                extension.equals("jpeg") ||
                extension.equals("png") ||
                extension.equals("gif") ||
                extension.equals("webp");
    }

    /**
     * Generate unique filename to avoid conflicts
     */
    private String generateUniqueFileName(String originalFileName) {
        String extension = "";
        if (originalFileName.contains(".")) {
            extension = originalFileName.substring(originalFileName.lastIndexOf("."));
        }
        return "product-" + System.currentTimeMillis() + extension;
    }

    @Override
    protected void doOptions(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setStatus(HttpServletResponse.SC_OK);
    }
}