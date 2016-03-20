package org.corpus_tools.peppermodules.infoModules.tests;

import java.io.File;
import java.io.IOException;
import java.lang.ref.PhantomReference;

import org.apache.commons.io.FileUtils;
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
import java.util.regex.Pattern;
import java.util.concurrent.TimeUnit;
import org.junit.*;
import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;
import org.openqa.selenium.*;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.phantomjs.PhantomJSDriver;
import org.openqa.selenium.support.ui.Select;

public class SeleniumTest extends PepperExporterTest {

	public SeleniumTest() {
		super.setFixture(new SaltInfoExporter());
		super.getFixture().setSaltProject(SaltFactory.createSaltProject());

		// set formats to support
		this.supportedFormatsCheck.add(new FormatDesc().setFormatName(PepperModule.ENDING_XML).setFormatVersion("1.0"));
		this.supportedFormatsCheck.add(new FormatDesc().setFormatName("html").setFormatVersion("5.0"));
	}

	private WebDriver driver;
	private boolean acceptNextAlert = true;
	private StringBuffer verificationErrors = new StringBuffer();

	URI exportFolder = getTempURI("SaltInfoTest/selenium");

	@After
	public void tearDown() throws Exception {
		if (driver != null) {
			driver.quit();
		}
		String verificationErrorString = verificationErrors.toString();
		if (!"".equals(verificationErrorString)) {
			fail(verificationErrorString);
		}
	}

	@Test
	public void test_convertPcc2CorpusWithoutCustomizations() throws IOException {
		URI importFolder = URI.createFileURI(PepperTestUtil.getTestResources() + "/selenium/salt");

		getFixture().getSaltProject().loadSaltProject(importFolder);

		getFixture().setCorpusDesc(new CorpusDesc().setFormatDesc(new FormatDesc().setFormatName(SaltInfoExporter.MODULE_NAME)).setCorpusPath(exportFolder));

//		FileUtils.deleteDirectory(new File(exportFolder.toFileString()));
//		start();

		URI corpusSiteURL = exportFolder.appendSegment("index.html");

		assertTrue(new File(corpusSiteURL.toFileString()).exists());

		// driver = new FirefoxDriver();
		driver = new PhantomJSDriver();
		driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);

		driver.get("file://" + corpusSiteURL.toFileString());

		assertEquals("pcc2", driver.getTitle());
		assertEquals("pcc2", driver.findElement(By.id("project_title")).getText());
		assertEquals("short description of myCorpus", driver.findElement(By.id("project_tagline")).getText());

		// check navigation
		assertTrue("Text was '" + driver.findElement(By.xpath(".//*[@id='navigation']/span")).getText() + "'", "Home".contains(driver.findElement(By.xpath(".//*[@id='navigation']/span")).getText()));

		// check dynamic navigation
		assertTrue("11299".contains(driver.findElement(By.xpath(".//*[@id='j1_2']/a")).getText()));
		assertTrue("4282".contains(driver.findElement(By.xpath(".//*[@id='j1_3']/a")).getText()));

		// open aggregation for super corpus pcc2
		driver.findElement(By.linkText("pcc2")).click();
		assertEquals("pcc2", driver.findElement(By.id("title")).getText());
		assertEquals("func", driver.findElement(By.xpath(".//*[@id='content']/div[3]/table/tbody/tr[1]/td[1]/span/span[1]")).getText());
		assertEquals("AC", driver.findElement(By.xpath(".//*[@id='func_values']/span[1]/span[1]")).getText());
		
		//check expand/collapse for annotation value
		assertEquals(5, driver.findElements(By.xpath(".//*[@id='func_values']/span")).size());
		driver.findElement(By.xpath(".//*[@id='func_btn']")).click();
		assertEquals(52, driver.findElements(By.xpath(".//*[@id='func_values']/span")).size());
		driver.findElement(By.xpath(".//*[@id='func_btn']")).click();
		assertEquals(5, driver.findElements(By.xpath(".//*[@id='func_values']/span")).size());
		
		//does not work, since the id of tooltip is changing
//		//check tooltip
//		Actions action = new Actions(driver);
//		action.moveToElement(driver.findElement(By.xpath(".//*[@id='content']/table/tbody/tr[2]/td[1]/span/i"))).build().perform();
//		assertEquals("Total number of nodes in the current document or corpus. An SNode is an abstract node which could be instantiated as e.g. SToken, SSpan, SStructure, STextualDS and so on.",driver.findElement(By.xpath(".//*[@id='ui-tooltip-45']/div")).getText());
	}

	private boolean isElementPresent(By by) {
		try {
			driver.findElement(by);
			return true;
		} catch (NoSuchElementException e) {
			return false;
		}
	}

	private boolean isAlertPresent() {
		try {
			driver.switchTo().alert();
			return true;
		} catch (NoAlertPresentException e) {
			return false;
		}
	}

	private String closeAlertAndGetItsText() {
		try {
			Alert alert = driver.switchTo().alert();
			String alertText = alert.getText();
			if (acceptNextAlert) {
				alert.accept();
			} else {
				alert.dismiss();
			}
			return alertText;
		} finally {
			acceptNextAlert = true;
		}
	}

	// @Test
	// public void test1Doc1Text() throws IOException {
	// URI importFolder=
	// URI.createFileURI(PepperTestUtil.getTestResources()+"/selenium/salt");
	// URI customFolder=
	// URI.createFileURI(PepperTestUtil.getTestResources()+"/selenium/custom");
	// getFixture().getSaltProject().loadSaltProject(importFolder);
	//
	// getFixture().setCorpusDesc(new CorpusDesc().setFormatDesc(new
	// FormatDesc().setFormatName(SaltInfoExporter.MODULE_NAME)).setCorpusPath(getTempURI("SaltInfoTest/selenium")));
	// PepperModuleProperties prop= new PepperModuleProperties();
	// prop.setPropertyValue("pepper.after.copyRes",
	// "./custom/customization.json->./site/;./custom/impressum.html->./site/");
	// ((PepperModuleProperty<String>)getFixture().getProperties().getProperty("pepper.after.copyRes")).setValue("./custom/customization.json->./site/;./custom/impressum.html->./site/");
	//
	// start();
	//
	//
	// }
}
