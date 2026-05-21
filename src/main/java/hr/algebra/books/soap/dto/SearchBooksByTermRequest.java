package hr.algebra.books.soap.dto;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "searchBooksByTermRequest", namespace = "http://algebra.hr/books/soap")
@XmlAccessorType(XmlAccessType.FIELD)
public class SearchBooksByTermRequest {

    private String term;

    public String getTerm() { return term; }
    public void setTerm(String term) { this.term = term; }
}
