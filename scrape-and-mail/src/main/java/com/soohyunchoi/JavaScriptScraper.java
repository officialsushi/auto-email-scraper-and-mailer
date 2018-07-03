package com.soohyunchoi;

import junit.framework.TestCase;
import org.junit.*;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeDriverService;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.File;

/**
 * Scraper using Selenium to scrape off javascript sites
 * especially useful for CloudFare email protection
 */

public class JavaScriptScraper extends TestCase {
	private static ChromeDriverService service;
	private WebDriver driver;
	
	public JavaScriptScraper() throws Exception{
		createAndStartService();
		createDriver();
	}
	
	@BeforeClass
	public static void createAndStartService() throws Exception{
		service = new ChromeDriverService.Builder()
				.usingDriverExecutable(new File("/Applications/WebDrivers/chromedriver.exe/"))
				.usingAnyFreePort()
				.build();
		service.start();
	}
	
	@AfterClass
	public static void StopService() {
		service.stop();
	}
	
	@Before
	public void createDriver() {
		ChromeOptions chromeOptions = new ChromeOptions();
		chromeOptions.addArguments("--headless");
		chromeOptions.addArguments("start-maximized");
		chromeOptions.addArguments("--disable-gpu");
		chromeOptions.addArguments("--disable-extensions");
		chromeOptions.addArguments("--load-images=no");
		
		driver = new ChromeDriver(chromeOptions);
	}
	@After
	public void quitDriver() {
		driver.quit();
	}
	
	@Test
	public void testGoogleSearch() {
		driver.get("http://www.google.com");
	}
	private void scrape(String url){
		driver.get(url);
	}
	
}
