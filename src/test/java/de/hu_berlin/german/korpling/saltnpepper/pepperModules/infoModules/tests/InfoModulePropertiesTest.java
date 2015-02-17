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
/**
 * 
 */
package de.hu_berlin.german.korpling.saltnpepper.pepperModules.infoModules.tests;

import org.eclipse.emf.common.util.URI;
import org.junit.Before;
import org.junit.Test;

import de.hu_berlin.german.korpling.saltnpepper.pepperModules.infoModules.SaltInfoProperties;

/**
 * @author jschmolling
 *
 */
public class InfoModulePropertiesTest {
	
	
	public static final URI PROP_URI = URI.createURI("./src/main/resources/sample-config/default.properties");
	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
	
	}

	@Test
	public void testLoadSampleProperties() {
		SaltInfoProperties properties = new SaltInfoProperties();
		properties.addProperties(PROP_URI);
	}

}
