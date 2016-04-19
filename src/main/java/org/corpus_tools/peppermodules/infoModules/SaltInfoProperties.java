/**
 * Copyright 2009 Humboldt-Universität zu Berlin, INRIA.
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
package org.corpus_tools.peppermodules.infoModules;

import org.corpus_tools.pepper.modules.PepperModuleProperties;
import org.corpus_tools.pepper.modules.PepperModuleProperty;

/**
 * 
 * @author Florian Zipser
 * @author Jakob Schmolling
 *
 */
@SuppressWarnings("serial")
public class SaltInfoProperties extends PepperModuleProperties {

	public final static String PROP_HTML_OUTPUT = "htmlOutput";
	/**
	 * Property name of the theme
	 */
	public final static String PROP_THEME = "theme";
	public static final String THEME_DEFAULT = "default";
	public static final String THEME_HISTORIC = "historic";
	public static final String PROP_HTML_INTERPRETED = "htmlInterpreted";

	public SaltInfoProperties() {
		this.addProperty(new PepperModuleProperty<Boolean>(PROP_HTML_OUTPUT, Boolean.class, "Determines whether the SaltInfoExporter should produce a corpus site in html. the deafult is 'true', which produces an html output, change this to 'false' to avoid html output. ", true, false));
		this.addProperty(new PepperModuleProperty<String>(PROP_THEME, String.class, "Determines the theme of the output HTML project. The theme could be 'historic' or 'default'. ", THEME_DEFAULT, false));
		this.addProperty(new PepperModuleProperty<Boolean>(PROP_HTML_INTERPRETED, Boolean.class, "Determines whether the short description in the html output will be interpreted as html code. The default is 'false' ", false, false));
	}

	public Boolean isHtmlOutput() {
		return (Boolean) this.getProperty(PROP_HTML_OUTPUT).getValue();
	}

	public String getTheme() {
		return (String) this.getProperty(PROP_THEME).getValue();
	}

	public Boolean getHtmlInterpretation() {
		return (Boolean) this.getProperty(PROP_HTML_INTERPRETED).getValue();
	}
}
