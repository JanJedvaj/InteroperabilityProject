package hr.algebra.books.xmlvalidation.service;

import hr.algebra.books.xmlvalidation.dto.ValidationError;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import java.util.ArrayList;
import java.util.List;

/**
 * SAX ErrorHandler that collects all errors without throwing,
 * so validation can report the full list of problems in one pass.
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
        // Re-throw so the validator stops on truly unrecoverable XML
        throw e;
    }

    public List<ValidationError> getErrors() { return errors; }
    public boolean isValid() { return errors.isEmpty(); }
}
