package de.hu_berlin.german.korpling.saltnpepper.pepperModules.infoModules.tests;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.junit.Before;
import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import de.hu_berlin.german.korpling.saltnpepper.pepper.testFramework.PepperModuleTest;
import de.hu_berlin.german.korpling.saltnpepper.pepperModules.infoModules.SaltInfoDictionary;
import de.hu_berlin.german.korpling.saltnpepper.pepperModules.infoModules.StructuralInfo;

public class StructuralInfoTest {

	private StructuralInfo fixture= null;
	
	public StructuralInfo getFixture() {
		return fixture;
	}

	public void setFixture(StructuralInfo fixture) {
		this.fixture = fixture;
	}

	@Before
	public void setUp() throws Exception {
		setFixture(new StructuralInfo());
		XMLOutputFactory xof = XMLOutputFactory.newInstance();
		fixtureFile= new File(PepperModuleTest.getTempPath_static("saltInfoExporter").getAbsolutePath()+"/structuralInfo.xml");
        xml = xof.createXMLStreamWriter(new FileWriter(fixtureFile));
        
        DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
		builder = builderFactory.newDocumentBuilder();
		
		XPathFactory xPathfactory = XPathFactory.newInstance();
		xpath = xPathfactory.newXPath();
	}
	
	private File fixtureFile= null;
	private XMLStreamWriter xml= null;
	private DocumentBuilder builder = null;
	private XPath xpath= null;
	/**
	 * Writes an empty structural info tag.
	 * @throws XMLStreamException
	 * @throws FileNotFoundException
	 * @throws SAXException
	 * @throws IOException
	 * @throws ParserConfigurationException
	 * @throws XPathExpressionException
	 */
	@Test
	public void testWrite_empty() throws XMLStreamException, FileNotFoundException, SAXException, IOException, ParserConfigurationException, XPathExpressionException {
		xml.writeStartDocument();
		getFixture().write(xml);
		xml.writeEndDocument();
		xml.flush();
		
		Document document = builder.parse(new FileInputStream(fixtureFile));
		XPathExpression expr= xpath.compile("//node()");
		NodeList nodes = (NodeList)expr.evaluate(document, XPathConstants.NODESET);
		
		assertEquals(1, nodes.getLength());
		assertEquals(SaltInfoDictionary.TAG_STRUCTURAL_INFO, nodes.item(0).getNodeName());
	}
	
	/**
	 * Writes a filled structural info tag.
	 * @throws XMLStreamException
	 * @throws FileNotFoundException
	 * @throws SAXException
	 * @throws IOException
	 * @throws ParserConfigurationException
	 * @throws XPathExpressionException
	 */
	@Test
	public void testWrite_full() throws XMLStreamException, FileNotFoundException, SAXException, IOException, ParserConfigurationException, XPathExpressionException {
		getFixture().occurance_SNode= 1;
		getFixture().occurance_STextualDS= 2;
		getFixture().occurance_STimeline= 3;
		getFixture().occurance_SToken= 4;
		getFixture().occurance_SSpan= 5;
		getFixture().occurance_SStructure= 6;
		
		getFixture().occurance_SRelation= 10;
		getFixture().occurance_STimelineRelation= 11;
		getFixture().occurance_STextualRelation= 12;
		getFixture().occurance_SSpanningRelation= 13;
		getFixture().occurance_SDominanceRelation= 14;
		getFixture().occurance_SPointingRelation= 15;
		getFixture().occurance_SOrderRelation= 16;
		
		xml.writeStartDocument();
		getFixture().write(xml);
		xml.writeEndDocument();
		xml.flush();
		
		Document document = builder.parse(new FileInputStream(fixtureFile));
		assertEquals(StructuralInfo.KEY_SNODE, xpath.evaluate("//structuralInfo/entry[1]/@key", document, XPathConstants.STRING));
		assertEquals("1", xpath.evaluate("//structuralInfo/entry[1]/text()", document, XPathConstants.STRING));
		assertEquals(StructuralInfo.KEY_STIMELINE, xpath.evaluate("//structuralInfo/entry[2]/@key", document, XPathConstants.STRING));
		assertEquals("3", xpath.evaluate("//structuralInfo/entry[2]/text()", document, XPathConstants.STRING));
		assertEquals(StructuralInfo.KEY_STEXTUAL_DS, xpath.evaluate("//structuralInfo/entry[3]/@key", document, XPathConstants.STRING));
		assertEquals("2", xpath.evaluate("//structuralInfo/entry[3]/text()", document, XPathConstants.STRING));
		assertEquals(StructuralInfo.KEY_STOKEN, xpath.evaluate("//structuralInfo/entry[4]/@key", document, XPathConstants.STRING));
		assertEquals("4", xpath.evaluate("//structuralInfo/entry[4]/text()", document, XPathConstants.STRING));
		assertEquals(StructuralInfo.KEY_SSPAN, xpath.evaluate("//structuralInfo/entry[5]/@key", document, XPathConstants.STRING));
		assertEquals("5", xpath.evaluate("//structuralInfo/entry[5]/text()", document, XPathConstants.STRING));
		assertEquals(StructuralInfo.KEY_SSTRUCTURE, xpath.evaluate("//structuralInfo/entry[6]/@key", document, XPathConstants.STRING));
		assertEquals("6", xpath.evaluate("//structuralInfo/entry[6]/text()", document, XPathConstants.STRING));
		
		assertEquals(StructuralInfo.KEY_SRELATION, xpath.evaluate("//structuralInfo/entry[7]/@key", document, XPathConstants.STRING));
		assertEquals("10", xpath.evaluate("//structuralInfo/entry[7]/text()", document, XPathConstants.STRING));
		assertEquals(StructuralInfo.KEY_STEXTUAL_RELATION, xpath.evaluate("//structuralInfo/entry[8]/@key", document, XPathConstants.STRING));
		assertEquals("12", xpath.evaluate("//structuralInfo/entry[8]/text()", document, XPathConstants.STRING));
		assertEquals(StructuralInfo.KEY_STIMELINE_RELATION, xpath.evaluate("//structuralInfo/entry[9]/@key", document, XPathConstants.STRING));
		assertEquals("11", xpath.evaluate("//structuralInfo/entry[9]/text()", document, XPathConstants.STRING));
		assertEquals(StructuralInfo.KEY_SSPANNING_RELATION, xpath.evaluate("//structuralInfo/entry[10]/@key", document, XPathConstants.STRING));
		assertEquals("13", xpath.evaluate("//structuralInfo/entry[10]/text()", document, XPathConstants.STRING));
		assertEquals(StructuralInfo.KEY_SDOMINANCE_RELATION, xpath.evaluate("//structuralInfo/entry[11]/@key", document, XPathConstants.STRING));
		assertEquals("14", xpath.evaluate("//structuralInfo/entry[11]/text()", document, XPathConstants.STRING));
		assertEquals(StructuralInfo.KEY_SORDER_RELATION, xpath.evaluate("//structuralInfo/entry[12]/@key", document, XPathConstants.STRING));
		assertEquals("16", xpath.evaluate("//structuralInfo/entry[12]/text()", document, XPathConstants.STRING));
		assertEquals(StructuralInfo.KEY_SPOINTING_RELATION, xpath.evaluate("//structuralInfo/entry[13]/@key", document, XPathConstants.STRING));
		assertEquals("15", xpath.evaluate("//structuralInfo/entry[13]/text()", document, XPathConstants.STRING));
	}

}
