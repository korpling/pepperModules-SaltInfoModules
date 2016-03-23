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
package org.corpus_tools.peppermodules.infoModules;

import static org.junit.Assert.assertEquals;

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
import org.corpus_tools.salt.SaltFactory;
import org.corpus_tools.salt.common.SCorpus;
import org.corpus_tools.salt.common.SDocument;
import org.corpus_tools.salt.samples.SampleGenerator;
import org.junit.Before;
import org.junit.Test;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

public class SCorpusInfoTest {

	private CorpusInfo fixture= null;
		
	public CorpusInfo getFixture() {
		return fixture;
	}

	public void setFixture(CorpusInfo fixture) {
		this.fixture = fixture;
	}

	@Before
	public void setUp() throws Exception {
		setFixture(new CorpusInfo());
		
		DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
		builder = builderFactory.newDocumentBuilder();
		
		XPathFactory xPathfactory = XPathFactory.newInstance();
		xpath = xPathfactory.newXPath();
	}
	private DocumentBuilder builder = null;
	private XPath xpath= null;
	
	/**
	 * Merges two documents and some own meta data.
	 * @throws IOException 
	 * @throws SAXException 
	 * @throws FileNotFoundException 
	 * @throws XPathExpressionException 
	 */
	@Test
	public void testWriteData() throws FileNotFoundException, SAXException, IOException, XPathExpressionException {
		SDocument sDocument= SaltFactory.createSDocument();
		SampleGenerator.createMorphologyAnnotations(sDocument);
		SampleGenerator.createSyntaxAnnotations(sDocument);
		SampleGenerator.createAnaphoricAnnotations(sDocument);
		sDocument.createMetaAnnotation(null, "att1", "value1");
		sDocument.createMetaAnnotation(null, "att2", "value2");
		DocumentInfo docInfo1= new DocumentInfo();
		docInfo1.retrieveData(sDocument);
		
		SDocument sDocument2= SaltFactory.createSDocument();
		SampleGenerator.createMorphologyAnnotations(sDocument2);
		SampleGenerator.createSyntaxAnnotations(sDocument2);
		SampleGenerator.createAnaphoricAnnotations(sDocument2);
		sDocument2.createMetaAnnotation(null, "att1", "value1");
		sDocument2.createMetaAnnotation(null, "att2", "value2");
		DocumentInfo docInfo2= new DocumentInfo();
		docInfo2.retrieveData(sDocument2);
		
		SCorpus sCorpus= SaltFactory.createSCorpus();
		sCorpus.createMetaAnnotation(null, "newMeta1", "metaValue1");
		sCorpus.createMetaAnnotation(null, "newMeta2", "metaValue2");
		
		getFixture().getContainerInfos().add(docInfo1);
		getFixture().getContainerInfos().add(docInfo2);
		
		File out= new File(PepperModuleTest.getTempPath_static("saltInfoExporter").getAbsoluteFile()+"/corpus.xml");
		getFixture().setExportFile(out);
		getFixture().write(sCorpus);
		
		Document document = builder.parse(new FileInputStream(out));
		//meta data
		assertEquals("metaValue1", xpath.evaluate("//sCorpusInfo/metaDataInfo/entry[@key='newMeta1']/text()", document, XPathConstants.STRING));
		assertEquals("metaValue2", xpath.evaluate("//sCorpusInfo/metaDataInfo/entry[@key='newMeta2']/text()", document, XPathConstants.STRING));
		assertEquals("value2", xpath.evaluate("//sCorpusInfo/metaDataInfo/entry[@key='att2']/text()", document, XPathConstants.STRING));
		assertEquals("value1", xpath.evaluate("//sCorpusInfo/metaDataInfo/entry[@key='att1']/text()", document, XPathConstants.STRING));
		
		//structural info
		assertEquals("50", xpath.evaluate("//sCorpusInfo/structuralInfo/entry[@key='SNode']/text()", document, XPathConstants.STRING));
		assertEquals("72", xpath.evaluate("//sCorpusInfo/structuralInfo/entry[@key='SRelation']/text()", document, XPathConstants.STRING));
		assertEquals("22", xpath.evaluate("//sCorpusInfo/structuralInfo/entry[@key='SToken']/text()", document, XPathConstants.STRING));
		assertEquals("2", xpath.evaluate("//sCorpusInfo/structuralInfo/entry[@key='SSpan']/text()", document, XPathConstants.STRING));
		assertEquals("44", xpath.evaluate("//sCorpusInfo/structuralInfo/entry[@key='SDominanceRelation']/text()", document, XPathConstants.STRING));
		assertEquals("2", xpath.evaluate("//sCorpusInfo/structuralInfo/entry[@key='SPointingRelation']/text()", document, XPathConstants.STRING));
		
		//annotationsInfo
		assertEquals("22", xpath.evaluate("//sCorpusInfo/sLayerInfo[@sName='morphology']/sAnnotationInfo[@sName='lemma']/@occurrence", document, XPathConstants.STRING));
		//tests some examples if they are aggregated correctly
		assertEquals("4", xpath.evaluate("//sCorpusInfo/sLayerInfo[@sName='morphology']/sAnnotationInfo[@sName='lemma']/sValue[text()='be']/@occurrence", document, XPathConstants.STRING));
		
		assertEquals("22", xpath.evaluate("//sCorpusInfo/sLayerInfo[@sName='morphology']/sAnnotationInfo[@sName='pos']/@occurrence", document, XPathConstants.STRING));
		//tests some examples if they are aggregated correctly
		assertEquals("2", xpath.evaluate("//sCorpusInfo/sLayerInfo[@sName='morphology']/sAnnotationInfo[@sName='pos']/sValue[text()='JJ']/@occurrence", document, XPathConstants.STRING));
		assertEquals("4", xpath.evaluate("//sCorpusInfo/sLayerInfo[@sName='morphology']/sAnnotationInfo[@sName='pos']/sValue[text()='VBZ']/@occurrence", document, XPathConstants.STRING));
		
		assertEquals("24", xpath.evaluate("//sCorpusInfo/sLayerInfo[@sName='syntax']/sAnnotationInfo[@sName='const']/@occurrence", document, XPathConstants.STRING));
		assertEquals("6", xpath.evaluate("//sCorpusInfo/sLayerInfo[@sName='syntax']/sAnnotationInfo[@sName='const']/sValue[text()='VP']/@occurrence", document, XPathConstants.STRING));
		assertEquals("4", xpath.evaluate("//sCorpusInfo/sLayerInfo[@sName='syntax']/sAnnotationInfo[@sName='const']/sValue[text()='S']/@occurrence", document, XPathConstants.STRING));
		assertEquals("2", xpath.evaluate("//sCorpusInfo/sLayerInfo[@sName='syntax']/sAnnotationInfo[@sName='const']/sValue[text()='ROOT']/@occurrence", document, XPathConstants.STRING));
	}

}
