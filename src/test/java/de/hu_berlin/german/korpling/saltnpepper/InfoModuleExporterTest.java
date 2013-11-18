package de.hu_berlin.german.korpling.saltnpepper;

import java.io.File;

import org.eclipse.emf.common.util.URI;

import de.hu_berlin.german.korpling.saltnpepper.pepper.pepperModules.CorpusDefinition;
import de.hu_berlin.german.korpling.saltnpepper.pepper.pepperModules.FormatDefinition;
import de.hu_berlin.german.korpling.saltnpepper.pepper.pepperModules.PepperModulesFactory;
import de.hu_berlin.german.korpling.saltnpepper.pepper.testSuite.moduleTests.PepperExporterTest;
import de.hu_berlin.german.korpling.saltnpepper.pepperModules.sampleModules.InfoModuleExporter;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCommon.SaltCommonFactory;
import de.hu_berlin.german.korpling.saltnpepper.salt.saltCommon.sCorpusStructure.SCorpusGraph;

public class InfoModuleExporterTest extends PepperExporterTest {
	public static final String FILE_TMP_DIR = "_TMP/";
	public static final File TMP_DIR = new File(FILE_TMP_DIR);
	public static final URI TMP_DIR_URI = URI.createFileURI(TMP_DIR.toURI().getRawPath() + File.separator);
	
	private FormatDefinition formatDef;

	@Override
	protected void setUp() throws Exception {
//		// TODO Auto-generated method stub
		super.setUp();
		formatDef= PepperModulesFactory
                .eINSTANCE.createFormatDefinition();
            formatDef.setFormatName("xml");
            formatDef.setFormatVersion("1.0");
		this.setFixture(new InfoModuleExporter());
		
		this.supportedFormatsCheck.add(formatDef);
		
		this.setFixture(new InfoModuleExporter());
//		URI tDir = URI.createFileURI("pepperModuleTestInfoModule/").resolve(TMP_DIR_URI);
//		(new File(tDir.toFileString())).mkdirs();
//		this.getFixture().setTemproraries(tDir);
	}
	
	
	public void testExportEmptyCorpus() throws Exception {
		CorpusDefinition corpDef = PepperModulesFactory.eINSTANCE.createCorpusDefinition();
		// Only xml is supported 
		corpDef.setFormatDefinition(formatDef);
		corpDef.setCorpusPath(TMP_DIR_URI);
		SCorpusGraph empty = SaltCommonFactory.eINSTANCE.createSCorpusGraph();
		this.getFixture().setCorpusDefinition(corpDef);
		this.getFixture().getSaltProject().getSCorpusGraphs().add(empty);
		this.run();
		
	}

}

