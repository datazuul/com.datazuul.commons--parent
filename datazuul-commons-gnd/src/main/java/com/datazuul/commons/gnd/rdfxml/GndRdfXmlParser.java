package com.datazuul.commons.gnd.rdfxml;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stax.StAXSource;
import javax.xml.transform.stream.StreamResult;
import org.jdom2.Attribute;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.Namespace;
import org.jdom2.input.SAXBuilder;
import org.xml.sax.SAXException;

public class GndRdfXmlParser {

  private static final Namespace NS_BIBO = Namespace.getNamespace("http://purl.org/ontology/bibo/");
  private static final Namespace NS_DC = Namespace.getNamespace("http://purl.org/dc/elements/1.1/");
  private static final Namespace NS_DCMITYPE = Namespace.getNamespace("http://purl.org/dc/dcmitype/");
  private static final Namespace NS_DCTERMS = Namespace.getNamespace("http://purl.org/dc/terms/");
  private static final Namespace NS_DBP = Namespace.getNamespace("http://dbpedia.org/property/");
  private static final Namespace NS_DNB_INTERN = Namespace.getNamespace("http://dnb.de/");
  private static final Namespace NS_DNBT = Namespace.getNamespace("http://d-nb.info/standards/elementset/dnb#");
  private static final Namespace NS_FOAF = Namespace.getNamespace("http://xmlns.com/foaf/0.1/");
  private static final Namespace NS_GBV = Namespace.getNamespace("http://purl.org/ontology/gbv/");
  private static final Namespace NS_GEO = Namespace.getNamespace("http://www.opengis.net/ont/geosparql#");
  private static final Namespace NS_GNDO = Namespace.getNamespace("http://d-nb.info/standards/elementset/gnd#");
  private static final Namespace NS_ISBD = Namespace.getNamespace("http://iflastandards.info/ns/isbd/elements/");
  private static final Namespace NS_LIB = Namespace.getNamespace("http://purl.org/library/");
  private static final Namespace NS_MARC_ROLE = Namespace.getNamespace("http://id.loc.gov/vocabulary/relators/");
  private static final Namespace NS_OWL = Namespace.getNamespace("http://www.w3.org/2002/07/owl#");
  private static final Namespace NS_RDAU = Namespace.getNamespace("http://rdaregistry.info/Elements/u/");
  private static final Namespace NS_RDF = Namespace.getNamespace("http://www.w3.org/1999/02/22-rdf-syntax-ns#");
  private static final Namespace NS_RDFS = Namespace.getNamespace("http://www.w3.org/2000/01/rdf-schema#");
  private static final Namespace NS_SF = Namespace.getNamespace("http://www.opengis.net/ont/sf#");
  private static final Namespace NS_SKOS = Namespace.getNamespace("http://www.w3.org/2004/02/skos/core#");
  private static final Namespace NS_UMBEL = Namespace.getNamespace("http://umbel.org/umbel#");
  private static final Namespace NS_V = Namespace.getNamespace("http://www.w3.org/2006/vcard/ns#");

  private static final String TYPE_DIFFERENTIATED_PERSON = "http://d-nb.info/standards/elementset/gnd#DifferentiatedPerson";

  public void parse(InputStream is) throws XMLStreamException, TransformerException, ParserConfigurationException, SAXException, IOException, JDOMException {
    XMLInputFactory xmlInputFactory = XMLInputFactory.newInstance();
    XMLStreamReader xsr = xmlInputFactory.createXMLStreamReader(is);
    xsr.nextTag(); // Advance to RDF element

    TransformerFactory tf = TransformerFactory.newInstance();
    Transformer t = tf.newTransformer();
    while (xsr.nextTag() == XMLStreamConstants.START_ELEMENT) {
      DOMResult domResult = new DOMResult();
      t.transform(new StAXSource(xsr), domResult);

      DOMSource domSource = new DOMSource(domResult.getNode());

      ByteArrayOutputStream baos = new ByteArrayOutputStream();
      StreamResult streamResult = new StreamResult(baos);
      t.transform(domSource, streamResult);
      String xml = baos.toString();

      handleDescription(xml);
    }
  }

  private void handleDescription(String xml) throws JDOMException, IOException {
    SAXBuilder builder = new SAXBuilder();
    Document doc = builder.build(new ByteArrayInputStream(xml.getBytes(StandardCharsets.UTF_8)));
    Element descriptionElement = doc.getRootElement();
    Element typeElement = descriptionElement.getChild("type", NS_RDF);
    if (typeElement != null) {
      Attribute resourceAttr = typeElement.getAttribute("resource", NS_RDF);
      final String type = resourceAttr.getValue();

      switch (type) {
        case TYPE_DIFFERENTIATED_PERSON:
          handleDifferentiatedPerson(descriptionElement);
          break;
        default:
//        System.err.println("Unknown description type " + type);
          break;
      }
    } else {
      System.err.println(descriptionElement.toString());
    }
  }

  private void handleDifferentiatedPerson(Element description) {
    DifferentiatedPerson person = new DifferentiatedPerson();

    // gndIdentifier
    String gndIdentifier = description.getChildText("gndIdentifier", NS_GNDO);
    person.setGndIdentifier(gndIdentifier);

    // firstname, surname
    String firstname = null;
    String surname = null;
    Element preferredNameEntityForPerson = description.getChild("preferredNameEntityForThePerson", NS_GNDO);
    if (preferredNameEntityForPerson != null) {
      Element desc = preferredNameEntityForPerson.getChild("Description", NS_RDF);
      firstname = desc.getChildText("forename", NS_GNDO);
      surname = desc.getChildText("surname", NS_GNDO);
    }
    person.setFirstname(firstname);
    person.setSurname(surname);

    // birth
    String dateOfBirth = description.getChildText("dateOfBirth", NS_GNDO);
    person.setDateOfBirth(dateOfBirth);

    // death
    String dateOfDeath = description.getChildText("dateOfDeath", NS_GNDO);
    person.setDateOfDeath(dateOfDeath);

    if (person.getFirstname() == null && person.getSurname() == null) {
      // may be an organisation and no person
    } else {
      handleDifferentiatedPerson(person);
    }
  }
  
  /**
   * Overridable method for further processing of a differentiated person.
   * Default: just print data to system out...
   * @param differentiatedPerson a differentiated person
   */
  public void handleDifferentiatedPerson(DifferentiatedPerson differentiatedPerson) {
    System.out.println("DifferentiatedPerson: " + differentiatedPerson);
  }
}
