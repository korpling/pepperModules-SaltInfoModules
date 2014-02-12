package de.hu_berlin.german.korpling.saltnpepper;

import java.io.File;
import java.lang.reflect.InvocationTargetException;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.eclipse.emf.common.notify.Adapter;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.TreeIterator;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EOperation;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.resource.Resource;

import de.hu_berlin.german.korpling.saltnpepper.pepper.pepperFW.PepperFWFactory;
import de.hu_berlin.german.korpling.saltnpepper.pepper.pepperModules.CorpusDefinition;
import de.hu_berlin.german.korpling.saltnpepper.pepper.pepperModules.FormatDefinition;
import de.hu_berlin.german.korpling.saltnpepper.pepper.pepperModules.PepperExporter;
import de.hu_berlin.german.korpling.saltnpepper.pepper.pepperModules.PepperModulesFactory;
import de.hu_berlin.german.korpling.saltnpepper.pepper.testSuite.moduleTests.PepperExporterTest;
import de.hu_berlin.german.korpling.saltnpepper.pepperModules.infoModules.InfoModuleExporter;
import de.hu_berlin.german.korpling.saltnpepper.salt.samples.SampleGenerator;

public class InfoModuleExporterTest extends PepperExporterTest {
	public static final String FILE_TMP_DIR = System.getProperty("java.io.tmpdir");
	public static final File TMP_DIR = new File(FILE_TMP_DIR);
	public static final URI TMP_DIR_URI = URI.createFileURI(TMP_DIR.toURI()
			.getRawPath() + File.separator);
	//
	private FormatDefinition formatDef;
	
	@Override
	protected void setFixture(PepperExporter fixture) {
		// TODO Auto-generated method stub
		super.setFixture(fixture);
		formatDef = PepperModulesFactory.eINSTANCE.createFormatDefinition();
		formatDef.setFormatName("xml");
		formatDef.setFormatVersion("1.0");
		this.supportedFormatsCheck.add(formatDef);
	}

	@Override
	protected void setUp() throws Exception {
		// // TODO Auto-generated method stub
		super.setUp();
		
		this.setFixture(new InfoModuleExporter());
		this.getFixture().setTemproraries(TMP_DIR_URI);
//		this.supportedFormatsCheck.add(formatDef);

//		this.setFixture(new InfoModuleExporter());
		// URI tDir =
		// URI.createFileURI("pepperModuleTestInfoModule/").resolve(TMP_DIR_URI);
		// (new File(tDir.toFileString())).mkdirs();
		// this.getFixture().setTemproraries(tDir);
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
	
	@SuppressWarnings("restriction")
	public void testSampleExport() throws Exception {
		PepperExporter exporter = getFixture();
		exporter.setSaltProject(SampleGenerator.createCompleteSaltproject());
		CorpusDefinition corpusDefinition = PepperFWFactory.eINSTANCE
			    .createCorpusDefinition();
		corpusDefinition.setCorpusPath(TMP_DIR_URI);
		exporter.setCorpusDefinition(corpusDefinition);
		this.start();
	}
	

	@Override
	public void testSetGetResources() {
		// TODO Auto-generated method stub
//		super.testSetGetResources();
	}
	
	@Override
	public void testSetGetTemproraries() {
		// TODO Auto-generated method stub
//		super.testSetGetTemproraries();
	}

}
