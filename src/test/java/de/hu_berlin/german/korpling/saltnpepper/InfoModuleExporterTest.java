package de.hu_berlin.german.korpling.saltnpepper;

import org.eclipse.emf.common.util.BasicEList;
import org.eclipse.emf.common.util.EList;

import de.hu_berlin.german.korpling.saltnpepper.pepper.pepperModules.FormatDefinition;
import de.hu_berlin.german.korpling.saltnpepper.pepper.pepperModules.PepperModulesFactory;
import de.hu_berlin.german.korpling.saltnpepper.pepper.testSuite.moduleTests.PepperExporterTest;

public class InfoModuleExporterTest {//extends PepperExporterTest {
//	@Override
	protected void setUp() throws Exception {
		// TODO Auto-generated method stub
//		super.setUp();
		FormatDefinition formatDef= PepperModulesFactory
                .eINSTANCE.createFormatDefinition();
            formatDef.setFormatName("info");
            formatDef.setFormatVersion("1.0");
            EList<FormatDefinition> list = new BasicEList<FormatDefinition>();
            list.add(formatDef);
//            this.supportedFormatsCheck = list;
	}

}

