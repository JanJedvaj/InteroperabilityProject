package hr.algebra.books.xmlvalidation.dto;

import java.util.List;

public record ValidationReport(boolean valid, List<ValidationError> errors, String xmlPreview) {}
