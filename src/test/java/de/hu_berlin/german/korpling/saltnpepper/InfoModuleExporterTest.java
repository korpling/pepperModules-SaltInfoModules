package de.hu_berlin.german.korpling.saltnpepper;

import java.io.File;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.eclipse.emf.common.util.URI;
import org.junit.Before;
import org.junit.Test;

import de.hu_berlin.german.korpling.saltnpepper.pepper.common.CorpusDesc;
import de.hu_berlin.german.korpling.saltnpepper.pepper.common.FormatDesc;
import de.hu_berlin.german.korpling.saltnpepper.pepper.modules.PepperExporter;
import de.hu_berlin.german.korpling.saltnpepper.pepper.testFramework.PepperExporterTest;
import de.hu_berlin.german.korpling.saltnpepper.pepperModules.infoModules.InfoModuleExporter;
import de.hu_berlin.german.korpling.saltnpepper.salt.samples.SampleGenerator;

public class InfoModuleExporterTest extends PepperExporterTest {
	public static final String FILE_TMP_DIR = System.getProperty("java.io.tmpdir");
	public static final File TMP_DIR = new File(FILE_TMP_DIR);
	public static final URI TMP_DIR_URI = URI.createFileURI(TMP_DIR.toURI()
			.getRawPath() + File.separator);
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
		URI sampleHTMLoutput = URI.createFileURI("sampleIndexHTMLoutput.html")
				.resolve(TMP_DIR_URI);
		URI sampleResource = URI.createFileURI(this.getClass()
				.getResource(sample).getFile());
		InfoModuleExporter exporter = new InfoModuleExporter();
		Transformer t = exporter.getInfo2index();

		applyTransformation(sampleHTMLoutput, sampleResource, t);
	}
	
	@Test
	public void testXSLTransformation_info2html() throws Exception {
		String sample = "/sample-data/InfoModuleTestData/SampleInfo.xml";
		URI sampleHTMLoutput = URI.createFileURI("sampleTableHTMLoutput.html")
				.resolve(TMP_DIR_URI);
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
		File tmpDir = new File(TMP_DIR_URI.toFileString());

		StreamSource source = new StreamSource(input);
		StreamResult result = new StreamResult(output);
		if (!tmpDir.exists()) {
			System.out.println("creating tmpDir");
			tmpDir.mkdir();
		}
		System.out.println("Transform to: " + sampleHTMLoutput);
		System.out.println(output.getParentFile().canWrite());

		System.out.println("from:         " + input);
		System.out.println(input.canRead());
		t.transform(source, result);
	}
	
	@Test
	public void testSampleExport() throws Exception {
		PepperExporter exporter = getFixture();
		exporter.setSaltProject(SampleGenerator.createCompleteSaltproject());
		CorpusDesc corpusDesc = new CorpusDesc();
		corpusDesc.setCorpusPath(TMP_DIR_URI);
		exporter.setCorpusDesc(corpusDesc);
		this.start();
	}
}
