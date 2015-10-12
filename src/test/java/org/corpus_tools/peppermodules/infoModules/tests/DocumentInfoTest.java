/**
 * Copyright 2009 Humboldt-Universität zu Berlin, INRIA.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 *
 */
package org.corpus_tools.peppermodules.infoModules.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.corpus_tools.pepper.testFramework.PepperModuleTest;
import org.corpus_tools.peppermodules.infoModules.DocumentInfo;
import org.corpus_tools.salt.SaltFactory;
import org.corpus_tools.salt.common.SDocument;
import org.corpus_tools.salt.samples.SampleGenerator;
import org.junit.Before;
import org.junit.Test;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

public class DocumentInfoTest {
	private DocumentInfo fixture= null; 
	
	public DocumentInfo getFixture() {
		return fixture;
	}

	public void setFixture(DocumentInfo fixture) {
		this.fixture = fixture;
	}

	@Before
	public void setUp() throws Exception {
		setFixture(new DocumentInfo());
		
		DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
		builder = builderFactory.newDocumentBuilder();
		
		XPathFactory xPathfactory = XPathFactory.newInstance();
		xpath = xPathfactory.newXPath();
	}

	private DocumentBuilder builder = null;
	private XPath xpath= null;
	
	/**
	 * Adds a {@link SDocument} to the fixture and checks whether the data are retrieved correctly.
	 * @throws IOException 
	 * @throws SAXException 
	 * @throws FileNotFoundException 
	 * @throws XPathExpressionException 
	 */
	@Test
	public void testRetrieveData() throws FileNotFoundException, SAXException, IOException, XPathExpressionException {
		SDocument sDocument= SaltFactory.createSDocument();
		SampleGenerator.createMorphologyAnnotations(sDocument);
		SampleGenerator.createSyntaxAnnotations(sDocument);
		SampleGenerator.createAnaphoricAnnotations(sDocument);
		sDocument.createMetaAnnotation(null, "att1", "value1");
		sDocument.createMetaAnnotation(null, "att2", "value2");
		
		getFixture().retrieveData(sDocument);
		
		assertNotNull(getFixture().getMetaDataInfo());
		assertEquals(2, getFixture().getMetaDataInfo().size());
		assertNotNull(getFixture().getMetaDataInfo().get("att1"));
		assertTrue(getFixture().getMetaDataInfo().get("att1").contains("value1"));
		assertNotNull(getFixture().getMetaDataInfo().get("att2"));
		assertTrue(getFixture().getMetaDataInfo().get("att2").contains("value2"));
		
		assertNotNull(getFixture().getStructuralInfo());
		
		assertEquals(new Integer(25), getFixture().getStructuralInfo().occurance_SNode);
		assertEquals(new Integer(36), getFixture().getStructuralInfo().occurance_SRelation);
		assertEquals(new Integer(11), getFixture().getStructuralInfo().occurance_SToken);
		assertEquals(new Integer(12), getFixture().getStructuralInfo().occurance_SStructure);
		assertEquals(new Integer(22), getFixture().getStructuralInfo().occurance_SDominanceRelation);
		assertEquals(new Integer(1), getFixture().getStructuralInfo().occurance_SPointingRelation);
		
		assertEquals(3, getFixture().getAnnotations().keySet().size());
		assertNotNull(getFixture().getAnnotations().get(DocumentInfo.NO_LAYER));
		
		assertNotNull(getFixture().getAnnotations().get("morphology"));
		assertEquals(new Integer(11), getFixture().getAnnotations().get("morphology").get("lemma").occurrence);
		//tests some examples if they are aggregated correctly
		assertEquals(new Integer(1), getFixture().getAnnotations().get("morphology").get("lemma").get("to"));
		assertEquals(new Integer(2), getFixture().getAnnotations().get("morphology").get("lemma").get("be"));
		assertEquals(new Integer(11), getFixture().getAnnotations().get("morphology").get("pos").occurrence);
		//tests some examples if they are aggregated correctly
		assertEquals(new Integer(1), getFixture().getAnnotations().get("morphology").get("pos").get("VB"));
		assertEquals(new Integer(2), getFixture().getAnnotations().get("morphology").get("pos").get("VBZ"));
		
		assertNotNull(getFixture().getAnnotations().get("syntax"));
		assertEquals(new Integer(12), getFixture().getAnnotations().get("syntax").get("const").occurrence);
		//tests some examples if they are aggregated correctly
		assertEquals(new Integer(3), getFixture().getAnnotations().get("syntax").get("const").get("VP"));
		assertEquals(new Integer(1), getFixture().getAnnotations().get("syntax").get("const").get("ROOT"));
		assertEquals(new Integer(2), getFixture().getAnnotations().get("syntax").get("const").get("ADJP"));
	}
	
	/**
	 * Adds a {@link SDocument} to the fixture and checks whether the data are retrieved correctly.
	 * @throws IOException 
	 * @throws SAXException 
	 * @throws FileNotFoundException 
	 * @throws XPathExpressionException 
	 */
	@Test
	public void testWriteData() throws FileNotFoundException, SAXException, IOException, XPathExpressionException {
		File out= new File(PepperModuleTest.getTempPath_static("saltInfoExporter").getAbsoluteFile()+"/saltInfo.xml");
		SDocument sDocument= SaltFactory.createSDocument();
		SampleGenerator.createMorphologyAnnotations(sDocument);
		SampleGenerator.createSyntaxAnnotations(sDocument);
		SampleGenerator.createAnaphoricAnnotations(sDocument);
		sDocument.createMetaAnnotation(null, "att1", "value1");
		sDocument.createMetaAnnotation(null, "att2", "value2");
		
		getFixture().setExportFile(out);
		getFixture().write(sDocument);
		
		Document document = builder.parse(new FileInputStream(out));
		
		//meta data
		assertEquals("value1", xpath.evaluate("//sDocumentInfo/metaDataInfo/entry[@key='att1']/text()", document, XPathConstants.STRING));
		assertEquals("value2", xpath.evaluate("//sDocumentInfo/metaDataInfo/entry[@key='att2']/text()", document, XPathConstants.STRING));
		
		//structural info
		assertEquals("25", xpath.evaluate("//sDocumentInfo/structuralInfo/entry[@key='SNode']/text()", document, XPathConstants.STRING));
		assertEquals("36", xpath.evaluate("//sDocumentInfo/structuralInfo/entry[@key='SRelation']/text()", document, XPathConstants.STRING));
		assertEquals("11", xpath.evaluate("//sDocumentInfo/structuralInfo/entry[@key='SToken']/text()", document, XPathConstants.STRING));
		assertEquals("1", xpath.evaluate("//sDocumentInfo/structuralInfo/entry[@key='SSpan']/text()", document, XPathConstants.STRING));
		assertEquals("22", xpath.evaluate("//sDocumentInfo/structuralInfo/entry[@key='SDominanceRelation']/text()", document, XPathConstants.STRING));
		assertEquals("1", xpath.evaluate("//sDocumentInfo/structuralInfo/entry[@key='SPointingRelation']/text()", document, XPathConstants.STRING));
		
		//annotationsInfo
		assertEquals("11", xpath.evaluate("//sDocumentInfo/sLayerInfo[@sName='morphology']/sAnnotationInfo[@sName='lemma']/@occurrence", document, XPathConstants.STRING));
		//tests some examples if they are aggregated correctly
		assertEquals("2", xpath.evaluate("//sDocumentInfo/sLayerInfo[@sName='morphology']/sAnnotationInfo[@sName='lemma']/sValue[text()='be']/@occurrence", document, XPathConstants.STRING));
		
		assertEquals("11", xpath.evaluate("//sDocumentInfo/sLayerInfo[@sName='morphology']/sAnnotationInfo[@sName='pos']/@occurrence", document, XPathConstants.STRING));
		//tests some examples if they are aggregated correctly
		assertEquals("1", xpath.evaluate("//sDocumentInfo/sLayerInfo[@sName='morphology']/sAnnotationInfo[@sName='pos']/sValue[text()='JJ']/@occurrence", document, XPathConstants.STRING));
		assertEquals("2", xpath.evaluate("//sDocumentInfo/sLayerInfo[@sName='morphology']/sAnnotationInfo[@sName='pos']/sValue[text()='VBZ']/@occurrence", document, XPathConstants.STRING));
		
		assertEquals("12", xpath.evaluate("//sDocumentInfo/sLayerInfo[@sName='syntax']/sAnnotationInfo[@sName='const']/@occurrence", document, XPathConstants.STRING));
		assertEquals("3", xpath.evaluate("//sDocumentInfo/sLayerInfo[@sName='syntax']/sAnnotationInfo[@sName='const']/sValue[text()='VP']/@occurrence", document, XPathConstants.STRING));
		assertEquals("2", xpath.evaluate("//sDocumentInfo/sLayerInfo[@sName='syntax']/sAnnotationInfo[@sName='const']/sValue[text()='S']/@occurrence", document, XPathConstants.STRING));
		assertEquals("1", xpath.evaluate("//sDocumentInfo/sLayerInfo[@sName='syntax']/sAnnotationInfo[@sName='const']/sValue[text()='ROOT']/@occurrence", document, XPathConstants.STRING));
	}
}
