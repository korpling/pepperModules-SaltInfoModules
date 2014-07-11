/**
 * Copyright 2009 Humboldt University of Berlin, INRIA.
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
package de.hu_berlin.german.korpling.saltnpepper.pepperModules.infoModules;

import static org.junit.Assert.*;

import java.io.File;
import java.io.FileOutputStream;
import java.net.URL;

import org.eclipse.emf.common.util.URI;
import org.junit.Before;
import org.junit.Test;

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
		InfoModuleProperties properties = new InfoModuleProperties();
		properties.addProperties(PROP_URI);
	}

}
