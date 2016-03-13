package org.corpus_tools.peppermodules.infoModules.tests;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;

import org.corpus_tools.pepper.common.CorpusDesc;
import org.corpus_tools.pepper.common.FormatDesc;
import org.corpus_tools.pepper.exceptions.PepperTestException;
import org.corpus_tools.pepper.modules.PepperModule;
import org.corpus_tools.pepper.modules.PepperModuleProperties;
import org.corpus_tools.pepper.modules.PepperModuleProperty;
import org.corpus_tools.pepper.modules.coreModules.DOTExporter;
import org.corpus_tools.pepper.testFramework.PepperExporterTest;
import org.corpus_tools.pepper.testFramework.PepperTestUtil;
import org.corpus_tools.peppermodules.infoModules.SaltInfoExporter;
import org.corpus_tools.salt.SaltFactory;
import org.eclipse.emf.common.util.URI;
import org.junit.Test;


public class SeleniumTest extends PepperExporterTest {

	public SeleniumTest() {
		super.setFixture(new SaltInfoExporter());
		super.getFixture().setSaltProject(SaltFactory.createSaltProject());

		// set formats to support
		this.supportedFormatsCheck.add(new FormatDesc().setFormatName(PepperModule.ENDING_XML).setFormatVersion("1.0"));
		this.supportedFormatsCheck.add(new FormatDesc().setFormatName("html").setFormatVersion("5.0"));
	}
	@Test
	public void convertPcc2CorpusWithoutCustomizations() throws IOException {
		URI importFolder= URI.createFileURI(PepperTestUtil.getTestResources()+"/selenium/salt");
		URI exportFolder= getTempURI("SaltInfoTest/selenium");
		getFixture().getSaltProject().loadSaltProject(importFolder);
		
		getFixture().setCorpusDesc(new CorpusDesc().setFormatDesc(new FormatDesc().setFormatName(SaltInfoExporter.MODULE_NAME)).setCorpusPath(exportFolder));
		
		start();
		
		URI corpusSiteURL= exportFolder.appendFragment("index.html");
		
		assertTrue(new File(corpusSiteURL.toFileString()).exists());
		
		
	}
	
//	@Test
//	public void test1Doc1Text() throws IOException {
//		URI importFolder= URI.createFileURI(PepperTestUtil.getTestResources()+"/selenium/salt");
//		URI customFolder= URI.createFileURI(PepperTestUtil.getTestResources()+"/selenium/custom");
//		getFixture().getSaltProject().loadSaltProject(importFolder);
//		
//		getFixture().setCorpusDesc(new CorpusDesc().setFormatDesc(new FormatDesc().setFormatName(SaltInfoExporter.MODULE_NAME)).setCorpusPath(getTempURI("SaltInfoTest/selenium")));
//		PepperModuleProperties prop= new PepperModuleProperties();
//		prop.setPropertyValue("pepper.after.copyRes", "./custom/customization.json->./site/;./custom/impressum.html->./site/");
//		((PepperModuleProperty<String>)getFixture().getProperties().getProperty("pepper.after.copyRes")).setValue("./custom/customization.json->./site/;./custom/impressum.html->./site/");
//		
//		start();
//
//		
//	}

}
