package com.soohyunchoi;

import junit.framework.TestCase;
import org.junit.*;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeDriverService;
import org.openqa.selenium.chrome.ChromeOptions;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;

/**
 * Scraper using Selenium to scrape off javascript sites
 * especially useful for CloudFare email protection
 */

public class JavaScriptScraper extends TestCase {
	private static ChromeDriverService service;
	private WebDriver driver;
	
	public JavaScriptScraper() throws java.io.IOException {
		createAndStartService();
		createDriver();
	}
	
	@BeforeClass
	private static void createAndStartService() throws java.io.IOException {
		service = new ChromeDriverService.Builder()
				.usingDriverExecutable(new File("/Applications/WebDrivers/chromedriver/"))
				.usingAnyFreePort()
				.build();
		service.start();
	}
	
	@AfterClass
	public static void stopService() {
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
	public void quitDriver() {
		driver.quit();
	}
	
	@Test
	private void testGoogleSearch() {
		driver.get("http://www.google.com");
	}
	
	public void connect(String url) {
//		driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);
		driver.manage().timeouts().pageLoadTimeout(200, TimeUnit.SECONDS);
		driver.get(url);
	}
	
	/**
	 * Fix for StaleElementReferenceException
	 * Thanks to http://darrellgrainger.blogspot.com/2012/06/staleelementexception.html
	 * @param by
	 * @return boolean if safe to proceed
	 */
	public boolean retryingFindClick(By by) {
		boolean result = false;
		int attempts = 0;
		while(attempts < 2) {
			try {
				driver.findElement(by).click();
				result = true;
				break;
			} catch(StaleElementReferenceException e) {
			}
			attempts++;
		}
		return result;
	}
	
	public String[] scrape() {
		List<WebElement> elementsRaw = driver.findElements(By.tagName("div"));
		ArrayList<String> elementsRawSplit = new ArrayList<>();
		for (WebElement a : elementsRaw){
			String[] splitElements = a.getText().split("\\s+");
			for(String b : splitElements){
				elementsRawSplit.add(b);
			}
		}
		String[] elementsStringArray = new String[elementsRawSplit.size()];
		elementsRawSplit.toArray(elementsStringArray);
//		for (String a : elementsRawSplit){
//			System.out.print(a + " // ");
//		}
		return elementsStringArray;
	}
	
	public String[] scrapeHref() {
		List<WebElement> elementsRaw = driver.findElements(By.tagName("a"));
		String[] elementsStringArray = new String[elementsRaw.size()];
		for (int i = 0; i < elementsStringArray.length; i++) {
			elementsStringArray[i] = elementsRaw.get(i).getAttribute("href");
		}
		return elementsStringArray;
	}
}
//		scraper.connect("http://www.hypergridbusiness.com/about/write-for-us/");

