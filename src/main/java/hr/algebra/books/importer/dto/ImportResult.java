package hr.algebra.books.importer.dto;

import java.util.List;

public record ImportResult(
        List<Long> savedIds,
        List<ValidationError> xmlErrors,
        List<ValidationError> jsonErrors
) {
    public boolean hasErrors() {
        return !xmlErrors.isEmpty() || !jsonErrors.isEmpty();
    }
}
