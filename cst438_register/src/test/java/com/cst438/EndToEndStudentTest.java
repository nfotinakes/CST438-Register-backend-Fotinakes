package com.cst438;

import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;


import com.cst438.domain.Student;
import com.cst438.domain.StudentRepository;

@SpringBootTest
public class EndToEndStudentTest {
	
	public static final String CHROME_DRIVER_FILE_LOCATION = "C:/chromedriver_win32/chromedriver.exe";

	public static final String URL = "http://localhost:3000";

	public static final String TEST_USER_EMAIL = "seleniumtest@csumb.edu";

	public static final String TEST_USER_NAME = "Selenium Student"; 

	public static final int SLEEP_DURATION = 1000; // 1 second.

	@Autowired
	StudentRepository studentRepository;

	
	// Test for successfully adding a new student to the system
	@Test
	public void addStudentTest() throws Exception {

		/*
		 * if test student is already in system, then delete the student.
		 */
		Student x = null;
		do {
			x = studentRepository.findByEmail(TEST_USER_EMAIL);
			if (x != null)
				studentRepository.delete(x);
		} while (x != null);

		// set the driver location and start driver
		//@formatter:off
		// browser	property name 				Java Driver Class
		// edge 	webdriver.edge.driver 		EdgeDriver
		// FireFox 	webdriver.firefox.driver 	FirefoxDriver
		// IE 		webdriver.ie.driver 		InternetExplorerDriver
		//@formatter:on

		System.setProperty("webdriver.chrome.driver", CHROME_DRIVER_FILE_LOCATION);
		ChromeOptions ops = new ChromeOptions();
		ops.addArguments("--remote-allow-origins=*");
		WebDriver driver = new ChromeDriver(ops);
		// Puts an Implicit wait for 10 seconds before throwing exception
		driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);

		try {

			driver.get(URL);
			Thread.sleep(SLEEP_DURATION);

			// Select the Add Student Button
			WebElement we = driver.findElement(By.id("addStudentButton"));
			we.click();
			Thread.sleep(SLEEP_DURATION);

			// enter student info and click Add button
			driver.findElement(By.xpath("//input[@name='name']")).sendKeys(TEST_USER_NAME);
			driver.findElement(By.xpath("//input[@name='email']")).sendKeys(TEST_USER_EMAIL);
			driver.findElement(By.xpath("//button[@id='Add']")).click();
			Thread.sleep(SLEEP_DURATION);
			
			// Check that successful toast message is displayed
			String toast_text = driver.findElement(By.cssSelector(".Toastify__toast-body div:nth-child(2)")).getText();
			assertEquals(toast_text, "Student successfully added");
			Thread.sleep(SLEEP_DURATION);

			// Check repository for student, should not be null
			Student student = studentRepository.findByEmail(TEST_USER_EMAIL);
			assertNotNull(student, "Student has been found in database");

		} catch (Exception ex) {
			throw ex;
		} finally {

			// clean up database.
			Student s = studentRepository.findByEmail(TEST_USER_EMAIL);
			if (s != null)
				studentRepository.delete(s);
			driver.close();
			driver.quit();
		}

	}
	
	
	// Test for correct error message when leaving name, email, or both fields blank
	// when attempting to add a student to the system
	@Test
	public void addStudentTestBlankFields() throws Exception {


		// set the driver location and start driver
		//@formatter:off
		// browser	property name 				Java Driver Class
		// edge 	webdriver.edge.driver 		EdgeDriver
		// FireFox 	webdriver.firefox.driver 	FirefoxDriver
		// IE 		webdriver.ie.driver 		InternetExplorerDriver
		//@formatter:on

		System.setProperty("webdriver.chrome.driver", CHROME_DRIVER_FILE_LOCATION);
		ChromeOptions ops = new ChromeOptions();
		ops.addArguments("--remote-allow-origins=*");
		WebDriver driver = new ChromeDriver(ops);
		// Puts an Implicit wait for 10 seconds before throwing exception
		driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);

		try {

			driver.get(URL);
			Thread.sleep(SLEEP_DURATION);

			// Select the Add Student Button
			WebElement we = driver.findElement(By.id("addStudentButton"));
			we.click();
			Thread.sleep(SLEEP_DURATION);
			
			// Try to submit with both email and name fields blank
			driver.findElement(By.xpath("//button[@id='Add']")).click();
			Thread.sleep(SLEEP_DURATION);
			
			// Check that toast error message shows correct blank field message
			String toast_text = driver.findElement(By.cssSelector(".Toastify__toast-body div:nth-child(2)")).getText();
			assertEquals(toast_text, "Name and Email fields must be entered");
			Thread.sleep(SLEEP_DURATION);
			
			// Select the Add Student Button
			we = driver.findElement(By.id("addStudentButton"));
			we.click();
			Thread.sleep(SLEEP_DURATION);
			
			// Check for just blank name field
			driver.findElement(By.xpath("//input[@name='email']")).sendKeys(TEST_USER_EMAIL);
			driver.findElement(By.xpath("//button[@id='Add']")).click();
			Thread.sleep(SLEEP_DURATION);
			
			// Same error message
			toast_text = driver.findElement(By.cssSelector(".Toastify__toast-body div:nth-child(2)")).getText();
			assertEquals(toast_text, "Name and Email fields must be entered");
			Thread.sleep(SLEEP_DURATION);
			
			// Select the Add Student Button
			we = driver.findElement(By.id("addStudentButton"));
			we.click();
			Thread.sleep(SLEEP_DURATION);
			
			// Check for just blank email field
			driver.findElement(By.xpath("//input[@name='name']")).sendKeys(TEST_USER_NAME);
			driver.findElement(By.xpath("//button[@id='Add']")).click();
			Thread.sleep(SLEEP_DURATION);
			
			// Same error message
			toast_text = driver.findElement(By.cssSelector(".Toastify__toast-body div:nth-child(2)")).getText();
			assertEquals(toast_text, "Name and Email fields must be entered");
			Thread.sleep(SLEEP_DURATION);
			

		} catch (Exception ex) {
			throw ex;
		} finally {
			driver.close();
			driver.quit();
		}

	}
	
	
	// This test will check for the correct error message when trying to add an existing student
	// NOTE: At the moment test adds a student, similar to addStudentTest method, to make sure
	// student is in system, and then attempts to add the same student again. This could maybe be improved.
	@Test
	public void addStudentTestExists() throws Exception {

		/*
		 * if test student is already in system, then delete the student.
		 */
		Student x = null;
		do {
			x = studentRepository.findByEmail(TEST_USER_EMAIL);
			if (x != null)
				studentRepository.delete(x);
		} while (x != null);

		// set the driver location and start driver
		//@formatter:off
		// browser	property name 				Java Driver Class
		// edge 	webdriver.edge.driver 		EdgeDriver
		// FireFox 	webdriver.firefox.driver 	FirefoxDriver
		// IE 		webdriver.ie.driver 		InternetExplorerDriver
		//@formatter:on

		System.setProperty("webdriver.chrome.driver", CHROME_DRIVER_FILE_LOCATION);
		ChromeOptions ops = new ChromeOptions();
		ops.addArguments("--remote-allow-origins=*");
		WebDriver driver = new ChromeDriver(ops);
		// Puts an Implicit wait for 10 seconds before throwing exception
		driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);

		try {

			driver.get(URL);
			Thread.sleep(SLEEP_DURATION);

			// Select the Add Student Button
			WebElement we = driver.findElement(By.id("addStudentButton"));
			we.click();
			Thread.sleep(SLEEP_DURATION);

			// enter student info and click Add button
			driver.findElement(By.xpath("//input[@name='name']")).sendKeys(TEST_USER_NAME);
			driver.findElement(By.xpath("//input[@name='email']")).sendKeys(TEST_USER_EMAIL);
			driver.findElement(By.xpath("//button[@id='Add']")).click();
			Thread.sleep(SLEEP_DURATION);
			
			// Check that successful toast message is displayed
			String toast_text = driver.findElement(By.cssSelector(".Toastify__toast-body div:nth-child(2)")).getText();
			assertEquals(toast_text, "Student successfully added");
			Thread.sleep(SLEEP_DURATION);

			// Check repository for student, should not be null
			Student student = studentRepository.findByEmail(TEST_USER_EMAIL);
			assertNotNull(student, "Student has been found in database");
			
			// Click the Add Student button again
			we = driver.findElement(By.id("addStudentButton"));
			we.click();
			Thread.sleep(SLEEP_DURATION);
			
			// Attempt to add the same student again
			driver.findElement(By.xpath("//input[@name='name']")).sendKeys(TEST_USER_NAME);
			driver.findElement(By.xpath("//input[@name='email']")).sendKeys(TEST_USER_EMAIL);
			driver.findElement(By.xpath("//button[@id='Add']")).click();
			Thread.sleep(SLEEP_DURATION);
			
			// Check that the correct toast message error appears
			toast_text = driver.findElement(By.cssSelector(".Toastify__toast-body div:nth-child(2)")).getText();
			assertEquals(toast_text, "That email already exists");
			Thread.sleep(SLEEP_DURATION);
			

		} catch (Exception ex) {
			throw ex;
		} finally {

			// clean up database.
			Student s = studentRepository.findByEmail(TEST_USER_EMAIL);
			if (s != null)
				studentRepository.delete(s);
			driver.close();
			driver.quit();
		}

	}
	
}
