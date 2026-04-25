package BlogApplication.BlogApplication.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ApiResponse {

    private boolean success;
    private String message;
    private Object data;        // holds any response data — User, Blog, List etc.

    // ---- Static factory methods — clean and easy to use ----

    // Success with data
    public static ApiResponse success(String message, Object data) {
        return new ApiResponse(true, message, data);
    }

    // Success without data
    public static ApiResponse success(String message) {
        return new ApiResponse(true, message, null);
    }

    // Failure
    public static ApiResponse failure(String message) {
        return new ApiResponse(false, message, null);
    }
}