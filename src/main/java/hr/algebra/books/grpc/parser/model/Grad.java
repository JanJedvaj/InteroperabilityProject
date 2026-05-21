package hr.algebra.books.grpc.parser.model;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;

@XmlAccessorType(XmlAccessType.FIELD)
public class Grad {

    @XmlElement(name = "GradIme")
    private String gradIme;

    @XmlElement(name = "Podaci")
    private Podaci podaci;

    public String getGradIme() { return gradIme; }
    public Podaci getPodaci() { return podaci; }
}
