package de.hu_berlin.german.korpling.saltnpepper.pepperModules.infoModules;

import static org.junit.Assert.assertTrue;

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
import de.hu_berlin.german.korpling.saltnpepper.pepper.testFramework.PepperExporterTest;
import de.hu_berlin.german.korpling.saltnpepper.salt.SaltFactory;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCommon.SaltProject;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCommon.sCorpusStructure.SCorpus;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCommon.sCorpusStructure.SCorpusGraph;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCommon.sCorpusStructure.SDocument;
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
		this.setFixture(new InfoModuleExporter());
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

		InfoModuleExporter exporter = new InfoModuleExporter();
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
		InfoModuleExporter exporter = new InfoModuleExporter();
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
		exporter.setSaltProject(SampleGenerator.createCompleteSaltproject2());
		CorpusDesc corpusDesc = new CorpusDesc();
		corpusDesc.setCorpusPath(URI.createFileURI("testSampleExport/").resolve(TMP_DIR_URI));
		exporter.setCorpusDesc(corpusDesc);
		this.start();
	}
	
	@Test
	public void testConcurency() throws Exception {
		PepperExporter exporter = getFixture();
		
		SaltProject p = createCompleteSaltprojectWithMetadata();
		SCorpus target = p.getSCorpusGraphs().get(0).getSCorpora().get(2);
		p.setSName("test-project");
		for (int i = 0; i < 8; i++) {
			SDocument d = SaltFactory.eINSTANCE.createSDocument();
			d.setId("id_doc" + i);
			d.setSName("empty_doc" + i);
			p.getSCorpusGraphs().get(0).addSDocument(target, d);
		}
		exporter.setSaltProject(p);
		CorpusDesc corpusDesc = new CorpusDesc();
		corpusDesc.setCorpusPath(URI.createFileURI("testConcurency/").resolve(TMP_DIR_URI));
		exporter.setCorpusDesc(corpusDesc);
		this.start();
	}

	private SaltProject createCompleteSaltprojectWithMetadata() {
		SaltProject project = SampleGenerator.createCompleteSaltproject();
		for (SCorpusGraph scorpGraph : project.getSCorpusGraphs()) {
			for (SDocument sdoc : scorpGraph.getSDocuments()) {
				SampleGenerator.addMetaAnnotation(sdoc, "sdoc_id", sdoc.getSId());
				SampleGenerator.addMetaAnnotation(sdoc, "same", "same");
			}
		}
		return project;
	}
	
	@Test
	public void testResourceExport() throws Exception {
		logger.debug(TMP_DIR_URI);
		URI resDir = URI.createURI("testResourceExport/").resolve(TMP_DIR_URI);
		logger.debug(resDir);
		InfoModuleExporter exporter = new InfoModuleExporter();
		exporter.copyRessourcesTo(resDir);
		for (String res : exporter.defaultResources) {
			File resFile = new File(resDir.toFileString(),res);
			assertTrue(resFile.canRead());
		}
	}
}
