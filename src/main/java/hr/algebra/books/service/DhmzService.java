package hr.algebra.books.service;

import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

@Service
public class DhmzService {

    private static final String DHMZ_URL = "https://vrijeme.hr/hrvatska_n.xml";

    public List<String[]> getTemperatureByCity(String cityName) throws Exception {
        List<String[]> results = new ArrayList<>();

        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document document = builder.parse(new URL(DHMZ_URL).openStream());
        document.getDocumentElement().normalize();

        NodeList gradovi = document.getElementsByTagName("Grad");

        for (int i = 0; i < gradovi.getLength(); i++) {
            Element grad = (Element) gradovi.item(i);

            NodeList imeList = grad.getElementsByTagName("GradIme");
            if (imeList.getLength() == 0) continue;

            String ime = imeList.item(0).getTextContent().trim();

            if (ime.toLowerCase().contains(cityName.toLowerCase())) {
                NodeList podatci = grad.getElementsByTagName("Podatci");
                String temperatura = "N/A";
                String opis = "N/A";

                if (podatci.getLength() > 0) {
                    Element podatak = (Element) podatci.item(0);

                    NodeList tempList = podatak.getElementsByTagName("Temp");
                    if (tempList.getLength() > 0) {
                        temperatura = tempList.item(0).getTextContent().trim();
                    }

                    NodeList vrijemeList = podatak.getElementsByTagName("Vrijeme");
                    if (vrijemeList.getLength() > 0) {
                        opis = vrijemeList.item(0).getTextContent().trim();
                    }
                }

                results.add(new String[]{ime, temperatura, opis});
            }
        }

        return results;
    }
}
