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
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.By;
import java.io.File;
import java.util.List;

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
	private static void createAndStartService() throws Exception{
		service = new ChromeDriverService.Builder()
				.usingDriverExecutable(new File("/Applications/WebDrivers/chromedriver/"))
				.usingAnyFreePort()
				.build();
		service.start();
	}
	
	@AfterClass
	private static void StopService() {
		service.stop();
	}
	
	@Before
	private void createDriver() {
		ChromeOptions chromeOptions = new ChromeOptions();
		chromeOptions.addArguments("--headless");
		chromeOptions.addArguments("start-maximized");
		chromeOptions.addArguments("--disable-gpu");
		chromeOptions.addArguments("--disable-extensions");
		chromeOptions.addArguments("--load-images=no");
		
		driver = new ChromeDriver(chromeOptions);
	}
	@After
	private void quitDriver() {
		driver.quit();
	}
	
	@Test
	private void testGoogleSearch() {
		driver.get("http://www.google.com");
	}
	public void scrape(String url){
		driver.get(url);
		String content = driver.getPageSource();
		List<WebElement> mainVersions = content.findElements(By.tagName("p"));
		System.out.println(content);
	}
	
	public static void main(String args[]) throws Exception{
		System.out.println("asf");
		JavaScriptScraper scraper = new JavaScriptScraper();
		scraper.scrape("http://www.dealermarketing.com/write-for-us/");
		scraper.scrape("http://www.hypergridbusiness.com/about/write-for-us/");
		scraper.quitDriver();
	}
}

