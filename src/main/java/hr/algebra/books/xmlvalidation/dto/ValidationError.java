package hr.algebra.books.xmlvalidation.dto;

public record ValidationError(int line, int column, String severity, String message) {}
