package de.hu_berlin.german.korpling.saltnpepper;

import java.io.File;

import org.eclipse.emf.common.util.URI;

import de.hu_berlin.german.korpling.saltnpepper.pepper.pepperModules.FormatDefinition;
import de.hu_berlin.german.korpling.saltnpepper.pepper.pepperModules.PepperModulesFactory;
import de.hu_berlin.german.korpling.saltnpepper.pepper.testSuite.moduleTests.PepperExporterTest;
import de.hu_berlin.german.korpling.saltnpepper.pepperModules.sampleModules.InfoModuleExporter;

public class InfoModuleExporterTest extends PepperExporterTest {
	public static final String FILE_TMP_DIR = "_TMP/";
	public static final File TMP_DIR = new File(FILE_TMP_DIR);
	public static final URI TMP_DIR_URI = URI.createFileURI(TMP_DIR.toURI().getRawPath() + File.separator);
	
	@Override
	protected void setUp() throws Exception {
		// TODO Auto-generated method stub
		super.setUp();
		FormatDefinition formatDef= PepperModulesFactory
                .eINSTANCE.createFormatDefinition();
            formatDef.setFormatName("info");
            formatDef.setFormatVersion("0.1");
		this.setFixture(new InfoModuleExporter());
		this.supportedFormatsCheck.add(formatDef);
		URI tDir = URI.createFileURI("pepperModuleTestInfoModule/").resolve(TMP_DIR_URI);
		(new File(tDir.toFileString())).mkdirs();
		this.getFixture().setTemproraries(tDir);
	}

}

