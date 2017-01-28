# DataZuul Commons: GND

This library provides tools for handling GND (Gemeinsame Normdatei) data from the German National Library.
Data is public available from [http://www.dnb.de](http://datendienst.dnb.de/cgi-bin/mabit.pl?userID=opendata&pass=opendata&cmd=login).

Data is available in several formats. As we deal with a format, this library will grow in specific parsers.

## RDF/XML
The RDF/XML file will be parsed using StAX following the [introduction to StAX](https://docs.oracle.com/javase/tutorial/jaxp/stax/).
Parsing is done using the iterator API, because as the tutorial states:

"In general, if you do not have a strong preference one way or the other, using the iterator API is recommended because it is more flexible and extensible, thereby 'future-proofing' your applications."

