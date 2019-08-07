# Alexandria Ingest GND: RDF Persons

This library provides tools for handling GND (Gemeinsame Normdatei) data from the German National Library.
Data is public available from [https://data.dnb.de/opendata/](https://data.dnb.de/opendata/).

Data is available in several formats. As we deal with a format, this library will grow in specific parsers.
First implementation uses the RDF/XML file `authorities-person_lds.rdf.gz` ("Stabiler Link auf den aktuellen Gesamtabzug der GND, Entitaeten Person (Person, Code p), Format RDF (RDF/XML)").

## RDF/XML

The RDF/XML file will be parsed using StAX following the [introduction to StAX](https://docs.oracle.com/javase/tutorial/jaxp/stax/).
Parsing is done using the Cursor API in combination with JDOM for handling detected description fragments.
