package de.hu_berlin.german.korpling.saltnpepper.pepperModules.infoModules;

import de.hu_berlin.german.korpling.saltnpepper.pepper.modules.PepperModuleProperties;
import de.hu_berlin.german.korpling.saltnpepper.pepper.modules.PepperModuleProperty;

public class InfoModuleProperties extends PepperModuleProperties {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8478680295397920096L;
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
