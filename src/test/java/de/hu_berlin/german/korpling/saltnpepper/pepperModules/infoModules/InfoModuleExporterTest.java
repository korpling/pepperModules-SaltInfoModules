/**
 * Copyright 2009 Humboldt University of Berlin, INRIA.
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
package de.hu_berlin.german.korpling.saltnpepper.pepperModules.infoModules;

import static org.junit.Assert.*;

import java.io.File;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.apache.log4j.Logger;
import org.eclipse.emf.common.util.URI;
import org.junit.Before;
import org.junit.Test;

import de.hu_berlin.german.korpling.saltnpepper.pepper.common.CorpusDesc;
import de.hu_berlin.german.korpling.saltnpepper.pepper.common.FormatDesc;
import de.hu_berlin.german.korpling.saltnpepper.pepper.modules.PepperExporter;
import de.hu_berlin.german.korpling.saltnpepper.pepper.modules.exceptions.PepperModuleException;
import de.hu_berlin.german.korpling.saltnpepper.pepper.testFramework.PepperExporterTest;
import de.hu_berlin.german.korpling.saltnpepper.salt.SaltFactory;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCommon.SaltProject;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCommon.sCorpusStructure.SCorpus;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCommon.sCorpusStructure.SCorpusGraph;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCommon.sCorpusStructure.SDocument;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCore.SMetaAnnotation;
import de.hu_berlin.german.korpling.saltnpepper.salt.samples.SampleGenerator;

public class InfoModuleExporterTest extends PepperExporterTest {
	private Logger logger = Logger.getLogger(InfoModuleExporterTest.class);
	
	public static final URI FILE_TMP_DIR = URI.createFileURI(System.getProperty("java.io.tmpdir"));
	public static final File TMP_DIR = new File(FILE_TMP_DIR.toFileString());
	public static final URI TMP_DIR_URI = (FILE_TMP_DIR.hasTrailingPathSeparator())?FILE_TMP_DIR:FILE_TMP_DIR.appendSegment("sds");
	//
	private FormatDesc formatDesc;

	@Before
	public void setUp() throws Exception {
		this.setFixture(new SaltInfoExporter());
		formatDesc = new FormatDesc();
		formatDesc.setFormatName("xml");
		formatDesc.setFormatVersion("1.0");
		this.supportedFormatsCheck.add(formatDesc);
		super.setResourcesURI(URI.createFileURI("./src/main/resources/"));
	}

	//
	//
	// public void testExportEmptyCorpus() throws Exception {
	// CorpusDefinition corpDef =
	// PepperModulesFactory.eINSTANCE.createCorpusDefinition();
	// // Only xml is supported
	// corpDef.setFormatDefinition(formatDef);
	// corpDef.setCorpusPath(TMP_DIR_URI);
	// SCorpusGraph empty = SaltCommonFactory.eINSTANCE.createSCorpusGraph();
	// this.getFixture().setCorpusDefinition(corpDef);
	// this.getFixture().getSaltProject().getSCorpusGraphs().add(empty);
	// this.run();
	//
	// }

	@Test
	public void testXSLTransformation_info2index() throws Exception {
		String sample = "/sample-data/InfoModuleTestData/SampleInfo.xml";
		URI testdir = URI.createFileURI("testXSLTransformation_info2index/").resolve(TMP_DIR_URI);
		URI sampleHTMLoutput = URI.createFileURI("sampleIndexHTMLoutput.html")
				.resolve(testdir);
		URI sampleResource = URI.createFileURI(this.getClass()
				.getResource(sample).getFile());

		SaltInfoExporter exporter = new SaltInfoExporter();
		Transformer t = exporter.getInfo2index();

		applyTransformation(sampleHTMLoutput, sampleResource, t);
	}
	
	@Test
	public void testXSLTransformation_info2html() throws Exception {
		String sample = "/sample-data/InfoModuleTestData/SampleInfo.xml";
		URI testdir = URI.createFileURI("testXSLTransformation_info2html/").resolve(TMP_DIR_URI);
		URI sampleHTMLoutput = URI.createFileURI("sampleTableHTMLoutput.html")
				.resolve(testdir);
		URI sampleResource = URI.createFileURI(this.getClass()
				.getResource(sample).getFile());
		SaltInfoExporter exporter = new SaltInfoExporter();
		Transformer t = exporter.getInfo2html();

		applyTransformation(sampleHTMLoutput, sampleResource, t);
	}

	private void applyTransformation(URI sampleHTMLoutput, URI sampleResource,
			Transformer t) throws TransformerException {
		File input = new File(sampleResource.toFileString());
		File output = new File(sampleHTMLoutput.toFileString());
		File tmpDir = output.getParentFile();

		StreamSource source = new StreamSource(input);
		StreamResult result = new StreamResult(output);
		if (!tmpDir.exists()) {
			logger.debug("creating tmpDir");
			tmpDir.mkdir();
		}
		logger.debug("Transform to: " + sampleHTMLoutput);
		logger.debug(output.getParentFile().canWrite());

		logger.debug("from:         " + input);
		logger.debug(input.canRead());
		t.transform(source, result);
	}
	
	@Test
	public void testSampleExport() throws Exception {
		PepperExporter exporter = getFixture();
		CorpusDesc corpusDesc = new CorpusDesc();
		SaltProject sProject = SampleGenerator.createCompleteSaltproject2();
		
		addMetaDataToEverySDocument(sProject);
		exporter.setSaltProject(sProject);
		corpusDesc.setCorpusPath(URI.createFileURI("testSampleExport/").resolve(TMP_DIR_URI));
		exporter.setCorpusDesc(corpusDesc);
		
		try {
			this.start();			
		} catch (PepperModuleException e) {
			fail();
		}
	}
	
	private void addMetaDataToEverySDocument(SaltProject sProject) {
		for (SCorpusGraph sCorpusGraph : sProject.getSCorpusGraphs()) {
			for (SCorpus sCorpus : sCorpusGraph.getSCorpora()) {
				SMetaAnnotation metaAnnotation = SaltFactory.eINSTANCE.createSMetaAnnotation();
				metaAnnotation.setSName("scorpmeta");
				metaAnnotation.setSValue(sCorpus.getSId());
				sCorpus.addSMetaAnnotation(metaAnnotation);				
			}
			for (SDocument sDocument : sCorpusGraph.getSDocuments()) {
				SMetaAnnotation metaAnnotation = SaltFactory.eINSTANCE.createSMetaAnnotation();
				metaAnnotation.setSName("sdocmeta");
				metaAnnotation.setSValue(sDocument.getSId());
				sDocument.addSMetaAnnotation(metaAnnotation);
			}
		}
	}

	@Test
	public void testConcurency() throws Exception {
		PepperExporter exporter = getFixture();
		
		SaltProject p = SampleGenerator.createCompleteSaltproject2();
		SCorpus target = p.getSCorpusGraphs().get(0).getSCorpora().get(2);
		p.setSName("test-project");
		for (int i = 0; i < 8; i++) {
			SDocument d = SaltFactory.eINSTANCE.createSDocument();
			d.setId("id_doc" + i);
			d.setSName("empty_doc" + i);
			p.getSCorpusGraphs().get(0).addSDocument(target, d);
		}
		addMetaDataToEverySDocument(p);
		exporter.setSaltProject(p);
		CorpusDesc corpusDesc = new CorpusDesc();
		corpusDesc.setCorpusPath(URI.createFileURI("testConcurency/").resolve(TMP_DIR_URI));
		exporter.setCorpusDesc(corpusDesc);
		try {
			this.start();			
		} catch (PepperModuleException e) {
			fail();
		}
	}

//	private SaltProject createCompleteSaltprojectWithMetadata() {
//		SaltProject project = SampleGenerator.createCompleteSaltproject();
//		for (SCorpusGraph scorpGraph : project.getSCorpusGraphs()) {
//			for (SDocument sdoc : scorpGraph.getSDocuments()) {
//				SampleGenerator.addMetaAnnotation(sdoc, "sdoc_id", sdoc.getSId());
//				SampleGenerator.addMetaAnnotation(sdoc, "same", "same");
//			}
//		}
//		return project;
//	}
	
	@Test
	public void testResourceExport() throws Exception {
		logger.debug(TMP_DIR_URI);
		URI resDir = URI.createURI("testResourceExport/").resolve(TMP_DIR_URI);
		logger.debug(resDir);
		SaltInfoExporter exporter = new SaltInfoExporter();
		exporter.copyRessourcesTo(resDir);
		for (String res : exporter.defaultResources) {
			File resFile = new File(resDir.toFileString(),res);
			assertTrue(resFile.canRead());
		}
	}
}
