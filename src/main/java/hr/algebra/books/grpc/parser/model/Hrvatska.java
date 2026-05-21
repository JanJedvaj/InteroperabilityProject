package hr.algebra.books.grpc.parser.model;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;

import java.util.ArrayList;
import java.util.List;

@XmlRootElement(name = "Hrvatska")
@XmlAccessorType(XmlAccessType.FIELD)
public class Hrvatska {

    @XmlElement(name = "DatumTermin")
    private String datumTermin;

    @XmlElement(name = "Grad")
    private List<Grad> gradovi = new ArrayList<>();

    public String getDatumTermin() { return datumTermin; }
    public List<Grad> getGradovi() { return gradovi; }
}
