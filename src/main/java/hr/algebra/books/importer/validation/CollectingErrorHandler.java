package hr.algebra.books.importer.validation;

import hr.algebra.books.importer.dto.ValidationError;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import java.util.ArrayList;
import java.util.List;

/**
 * SAX ErrorHandler that collects all XSD validation errors without stopping on the first one.
 * Intentionally separate from xmlvalidation's handler — the two features may diverge.
 */
public class CollectingErrorHandler implements ErrorHandler {

    private final List<ValidationError> errors = new ArrayList<>();

    @Override
    public void warning(SAXParseException e) {
        errors.add(new ValidationError(e.getLineNumber(), e.getColumnNumber(), "WARNING", e.getMessage()));
    }

    @Override
    public void error(SAXParseException e) {
        errors.add(new ValidationError(e.getLineNumber(), e.getColumnNumber(), "ERROR", e.getMessage()));
    }

    @Override
    public void fatalError(SAXParseException e) throws SAXException {
        errors.add(new ValidationError(e.getLineNumber(), e.getColumnNumber(), "FATAL", e.getMessage()));
        throw e;
    }

    public List<ValidationError> getErrors() { return errors; }
    public boolean isValid() { return errors.isEmpty(); }
}
