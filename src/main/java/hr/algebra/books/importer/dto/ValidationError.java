package hr.algebra.books.importer.dto;

public record ValidationError(int line, int column, String severity, String message) {}
