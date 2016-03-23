package org.corpus_tools.peppermodules.infoModules.ui;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import org.apache.commons.io.FileUtils;
import org.corpus_tools.pepper.common.CorpusDesc;
import org.corpus_tools.pepper.common.FormatDesc;
import org.corpus_tools.pepper.modules.PepperModule;
import org.corpus_tools.pepper.testFramework.PepperExporterTest;
import org.corpus_tools.pepper.testFramework.PepperTestUtil;
import org.corpus_tools.peppermodules.infoModules.SaltInfoExporter;
import org.corpus_tools.salt.SaltFactory;
import org.eclipse.emf.common.util.URI;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.phantomjs.PhantomJSDriver;

public class WhenExportingPcc2WithoutCustomizationTest {

	private static WebDriver driver;

	private static class SaltInfoExporterTest extends PepperExporterTest {
		URI exportFolder = getTempURI("SaltInfoTest/selenium");

		public SaltInfoExporterTest() {
			super.setFixture(new SaltInfoExporter());
			super.getFixture().setSaltProject(SaltFactory.createSaltProject());

			// set formats to support
			this.supportedFormatsCheck.add(new FormatDesc().setFormatName(PepperModule.ENDING_XML).setFormatVersion("1.0"));
			this.supportedFormatsCheck.add(new FormatDesc().setFormatName("html").setFormatVersion("5.0"));
		}

		public URI convert2SaltInfo() throws IOException {
			URI importFolder = URI.createFileURI(PepperTestUtil.getTestResources() + "/selenium/salt");
			getFixture().getSaltProject().loadSaltProject(importFolder);
			getFixture().setCorpusDesc(new CorpusDesc().setFormatDesc(new FormatDesc().setFormatName(SaltInfoExporter.MODULE_NAME)).setCorpusPath(exportFolder));
			FileUtils.deleteDirectory(new File(exportFolder.toFileString()));
			start();
			return exportFolder;
		}
	};

	@BeforeClass
	public static void setUpBeforeClass() throws IOException {
		SaltInfoExporterTest fixture = new SaltInfoExporterTest();
		URI corpusSiteFolder = fixture.convert2SaltInfo();

		driver = new PhantomJSDriver();
		driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);

		URI corpusSiteURL = corpusSiteFolder.appendSegment("index.html");
		assertTrue(new File(corpusSiteURL.toFileString()).exists());
		driver.get("file://" + corpusSiteURL.toFileString());
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		if (driver != null) {
			driver.quit();
		}
	}

	@Test
	public void shouldContainTitleAndDescriptionInHeader() {
		assertEquals("pcc2", driver.getTitle());
		assertEquals("pcc2", driver.findElement(By.id("project_title")).getText());
		assertEquals("short description of myCorpus", driver.findElement(By.id("project_tagline")).getText());
	}

	@Test
	public void shouldContainCorpusAndDocumentsInNavigation() {
		assertTrue("Text was '" + driver.findElement(By.xpath(".//*[@id='navigation']/span")).getText() + "'", "Home".contains(driver.findElement(By.xpath(".//*[@id='navigation']/span")).getText()));
		assertTrue("11299".contains(driver.findElement(By.xpath(".//*[@id='j1_2']/a")).getText()));
		assertTrue("4282".contains(driver.findElement(By.xpath(".//*[@id='j1_3']/a")).getText()));
	}

	@Test
	public void shouldContainSingleValuesInContent() {
		driver.findElement(By.linkText("pcc2")).click();
		assertEquals("pcc2", driver.findElement(By.id("title")).getText());
		assertEquals("func", driver.findElement(By.xpath(".//*[@id='content']/div[3]/table/tbody/tr[1]/td[1]/span/span[1]")).getText());
		assertEquals("AC", driver.findElement(By.xpath(".//*[@id='func_values']/span[1]/span[1]")).getText());
	}

	@Test
	public void shouldExpandAndCollapseAnnotationsInContent() {
		driver.findElement(By.linkText("pcc2")).click();

		assertEquals(5, driver.findElements(By.xpath(".//*[@id='func_values']/span")).size());
		driver.findElement(By.xpath(".//*[@id='func_btn']")).click();
		assertEquals(52, driver.findElements(By.xpath(".//*[@id='func_values']/span")).size());
		driver.findElement(By.xpath(".//*[@id='func_btn']")).click();
		assertEquals(5, driver.findElements(By.xpath(".//*[@id='func_values']/span")).size());
	}

	// @Test
	// public void whenHoverOverISymbol_shouldDisplayTooltip() throws
	// IOException {
	// driver.findElement(By.linkText("pcc2")).click();
	// // does not work, since the id of tooltip is changing
	// // //check tooltip
	// // Actions action = new Actions(driver);
	// //
	// action.moveToElement(driver.findElement(By.xpath(".//*[@id='content']/table/tbody/tr[2]/td[1]/span/i"))).build().perform();
	// // assertEquals("Total number of nodes in the current document or
	// // corpus. An SNode is an abstract node which could be instantiated as
	// // e.g. SToken, SSpan, SStructure, STextualDS and so
	// //
	// on.",driver.findElement(By.xpath(".//*[@id='ui-tooltip-45']/div")).getText());
	// }
}
