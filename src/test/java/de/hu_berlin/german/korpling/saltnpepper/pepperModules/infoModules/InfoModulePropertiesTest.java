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
