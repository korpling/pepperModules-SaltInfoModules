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
package org.corpus_tools.peppermodules.infoModules;

import static org.junit.Assert.assertEquals;

import org.eclipse.emf.common.util.URI;
import org.junit.Before;
import org.junit.Test;

/**
 * @author jschmolling
 *
 */
public class InfoModulePropertiesTest {

	private SaltInfoProperties fixture = null;

	public SaltInfoProperties getFixture() {
		return fixture;
	}

	public void setFixture(SaltInfoProperties fixture) {
		this.fixture = fixture;
	}

	public static final URI PROP_URI = URI.createURI("./src/main/resources/sample-config/default.properties");

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		setFixture(new SaltInfoProperties());
	}

	@Test
	public void testLoadSampleProperties() {
		SaltInfoProperties properties = new SaltInfoProperties();
		properties.addProperties(PROP_URI);
	}

	@Test
	public void testGetTheme_default() {
		assertEquals(SaltInfoProperties.THEME_DEFAULT, getFixture().getTheme());
	}

	@Test
	public void testGetTheme_historic() {
		getFixture().setPropertyValue(SaltInfoProperties.PROP_THEME, SaltInfoProperties.THEME_HISTORIC);
		assertEquals(SaltInfoProperties.THEME_HISTORIC, getFixture().getTheme());
	}

}
