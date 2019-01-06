package io.testproject.model;

/**
 * API error response data
 */
public class ApiErrorResponse {

    /**
     * The error message
     */
    private String error;

    /**
     * Additional data if available
     */
    private Object data;

    public Object getData() {
        return data;
    }

    public String getError() {
        return error != null ? error : "";
    }

    public void setError(String error) {
        this.error = error;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public ApiErrorResponse() {
    }

    public ApiErrorResponse(String error) {
        this.error = error;
    }
}
