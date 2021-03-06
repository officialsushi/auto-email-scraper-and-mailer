package com.soohyunchoi;

import junit.framework.TestCase;
import org.junit.*;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeDriverService;
import org.openqa.selenium.chrome.ChromeOptions;
import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Scraper using Selenium to scrape off javascript sites
 * especially useful for CloudFare email protection
 */
public class JavaScriptScraper extends TestCase {
	public static ChromeDriverService service;
	private static String OS = System.getProperty("os.name");
	private WebDriver driver;
	
	public JavaScriptScraper() {
		createDriver();
	}
	@BeforeClass
	public static void createAndStartService() throws java.io.IOException {
		// WINDOWS
		if (OS.indexOf("Win") >= 0) {
			service = new ChromeDriverService.Builder()
					.usingDriverExecutable(new File("chromedriver_win32.exe"))
					.usingAnyFreePort()
					.build();
		}
		// MAC OS
		else {
			service = new ChromeDriverService.Builder()
					.usingDriverExecutable(new File("chromedriver_mac64"))
					.usingAnyFreePort()
					.build();
		}
		service.start();
	}
	@AfterClass
	public static void stopService() { service.stop(); }
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
	public void quitDriver() { driver.quit(); }
	public void connect(String url) {
//		driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);
		driver.manage().timeouts().pageLoadTimeout(200, TimeUnit.SECONDS);
		driver.get(url);
	}
	public String[] scrape() {
		List<WebElement> elementsRaw = driver.findElements(By.tagName("div"));
		ArrayList<String> elementsRawSplit = new ArrayList();
		for (WebElement a : elementsRaw){
			String[] splitElements = a.getText().split("\\s+");
			for(String b : splitElements){
				elementsRawSplit.add(b);
			}
		}
		String[] elementsStringArray = new String[elementsRawSplit.size()];
		elementsRawSplit.toArray(elementsStringArray);
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

