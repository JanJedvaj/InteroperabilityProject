package hr.algebra.books.grpc.parser.model;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;

@XmlAccessorType(XmlAccessType.FIELD)
public class Podaci {

    @XmlElement(name = "Temp")
    private String temp;

    @XmlElement(name = "Vlaga")
    private String vlaga;

    @XmlElement(name = "TlakMBV")
    private String tlakMbv;

    @XmlElement(name = "Vjetar_brzina")
    private String vjetarBrzina;

    @XmlElement(name = "Vjetar_smjer")
    private String vjetarSmjer;

    public String getTemp() { return temp; }
    public String getVlaga() { return vlaga; }
    public String getTlakMbv() { return tlakMbv; }
    public String getVjetarBrzina() { return vjetarBrzina; }
    public String getVjetarSmjer() { return vjetarSmjer; }
}
