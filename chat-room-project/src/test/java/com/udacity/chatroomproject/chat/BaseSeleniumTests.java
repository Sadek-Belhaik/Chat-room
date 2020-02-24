package com.udacity.chatroomproject.chat;

import org.junit.After;
import org.junit.Before;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeDriverService;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.DesiredCapabilities;

import java.io.File;
import java.net.URL;
import java.util.Objects;

public abstract class BaseSeleniumTests {

    private static final String CHROMEDRIVER_BIN = "chromedriver";
    protected WebDriver driver;

    @Before
    public void setUp() {
        String driverFile = findFile();
        DesiredCapabilities capabilities = DesiredCapabilities.chrome();
        ChromeDriverService service = new ChromeDriverService.Builder().usingDriverExecutable(new File(driverFile)).build();
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--no-sandbox");
        options.addArguments("--headless");
        options.setExperimentalOption("useAutomationExtension", false);
        options.addArguments("start-maximized");
        options.addArguments("disable-infobars");
        options.addArguments("--disable-extensions");
        options.addArguments("--disable-gpu");
        options.addArguments("--disable-dev-shm-usage");
        options.merge(capabilities);
        this.driver = new ChromeDriver(service, options);
    }

    private String findFile() {
        ClassLoader classLoader = getClass().getClassLoader();
        URL url = classLoader.getResource(CHROMEDRIVER_BIN);
        return Objects.requireNonNull(url).getFile();
    }

    @After
    public void tearDown() {
        if (this.driver != null) {
            this.driver.close();
        }
    }

}
