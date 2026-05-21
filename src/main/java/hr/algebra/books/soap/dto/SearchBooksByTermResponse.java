package hr.algebra.books.soap.dto;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;

import java.util.ArrayList;
import java.util.List;

@XmlRootElement(name = "searchBooksByTermResponse", namespace = "http://algebra.hr/books/soap")
@XmlAccessorType(XmlAccessType.FIELD)
public class SearchBooksByTermResponse {

    @XmlElement(name = "book")
    private List<SoapBook> books = new ArrayList<>();

    public List<SoapBook> getBooks() { return books; }
    public void setBooks(List<SoapBook> books) { this.books = books; }
}
