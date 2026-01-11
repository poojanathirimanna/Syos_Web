package com.syos.web.application.dto;

public class ApiResponse {
    private boolean ok;
    private String message;
    private Object data;

    public ApiResponse() {
    }

    public ApiResponse(boolean ok, String message) {
        this.ok = ok;
        this.message = message;
    }

    public ApiResponse(boolean ok, String message, Object data) {
        this.ok = ok;
        this.message = message;
        this.data = data;
    }

    public static ApiResponse success(String message) {
        return new ApiResponse(true, message);
    }

    public static ApiResponse success(String message, Object data) {
        return new ApiResponse(true, message, data);
    }

    public static ApiResponse error(String message) {
        return new ApiResponse(false, message);
    }

    public boolean isOk() {
        return ok;
    }

    public void setOk(boolean ok) {
        this.ok = ok;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }
}

