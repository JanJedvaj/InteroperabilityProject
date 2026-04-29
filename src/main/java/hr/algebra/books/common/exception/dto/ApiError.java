package hr.algebra.books.common.exception.dto;

import java.time.Instant;
import java.util.List;

public record ApiError(
        int status,
        String error,
        String message,
        Instant timestamp,
        String path,
        List<String> fieldErrors
) {

    public ApiError(int status, String error, String message, Instant timestamp, String path) {
        this(status, error, message, timestamp, path, List.of());
    }
}
