package com.datazuul.commons.gnd.rdfxml;

import java.io.FileInputStream;
import java.io.InputStream;

import org.junit.*;

public class GndRdfXmlParserTest {
  
  public GndRdfXmlParserTest() {
  }
  
  @BeforeClass
  public static void setUpClass() {
  }
  
  @AfterClass
  public static void tearDownClass() {
  }
  
  @Before
  public void setUp() {
  }
  
  @After
  public void tearDown() {
  }

  /**
   * Test of parse method, of class GndRdfXmlParser.
   */
  @Test
  public void testParse() throws Exception {
    System.out.println("parse");
    InputStream is = this.getClass().getResourceAsStream("/gndRdfXml-testfile.xml");
    GndRdfXmlParser instance = new GndRdfXmlParser();
    instance.parse(is);
  }
  
  @Ignore
  @Test
  public void testParseOfBigFile() throws Exception {
    System.out.println("parse big file");
    InputStream is = new FileInputStream("/mnt/DATA/dev/data/authorities-person_lds_20190613.rdf");
    GndRdfXmlParser instance = new GndRdfXmlParser();
    instance.parse(is);
  }
}
