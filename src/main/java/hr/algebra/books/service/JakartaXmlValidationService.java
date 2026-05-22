package hr.algebra.books.service;

import hr.algebra.books.model.xml.BooksXml;
import jakarta.xml.bind.*;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import javax.xml.XMLConstants;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

@Service
public class JakartaXmlValidationService {

    public List<String> validateBooksXml(String xmlFilePath) {
        List<String> validationErrors = new ArrayList<>();

        try {
            JAXBContext context = JAXBContext.newInstance(BooksXml.class);

            SchemaFactory schemaFactory =
                SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
            Schema schema = schemaFactory.newSchema(
                new ClassPathResource("xsd/books-jaxb.xsd").getURL()
            );

            Unmarshaller unmarshaller = context.createUnmarshaller();
            unmarshaller.setSchema(schema);

            unmarshaller.setEventHandler(event -> {
                validationErrors.add(
                    "Linija " + event.getLocator().getLineNumber() +
                    ": " + event.getMessage()
                );
                return true;
            });

            unmarshaller.unmarshal(new File(xmlFilePath));

        } catch (JAXBException e) {
            validationErrors.add("JAXB greška: " + e.getMessage());
        } catch (Exception e) {
            validationErrors.add("Greška pri validaciji: " + e.getMessage());
        }

        return validationErrors;
    }
}
