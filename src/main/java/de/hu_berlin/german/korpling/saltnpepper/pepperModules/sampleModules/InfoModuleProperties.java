package de.hu_berlin.german.korpling.saltnpepper.pepperModules.sampleModules;

import de.hu_berlin.german.korpling.saltnpepper.pepper.pepperModules.PepperModuleProperties;
import de.hu_berlin.german.korpling.saltnpepper.pepper.pepperModules.PepperModuleProperty;

public class InfoModuleProperties extends PepperModuleProperties {

	public final static String HTML_OUTPUT = "htmlOutput";
	public final static String INCLUDE_XML_HEADER = "includeXMLHeader";

	public InfoModuleProperties() {
		this.addProperty(new PepperModuleProperty<Boolean>(INCLUDE_XML_HEADER,
				Boolean.class, "", false));
		this.addProperty(new PepperModuleProperty<Boolean>(HTML_OUTPUT,
				Boolean.class, "", true,true));
	}

	public Boolean getIncludeXMLHeader() {
		return (Boolean) this.getProperty(INCLUDE_XML_HEADER).getValue();
	}
	
	public Boolean hasHtmlOutput() {
		return (Boolean) this.getProperty(HTML_OUTPUT).getValue();
	}

	@Override
	public boolean checkProperty(PepperModuleProperty<?> prop) {
		// TODO Auto-generated method stub
		return super.checkProperty(prop);
	}
}
