package driver;

import com.google.common.collect.ImmutableMap;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.remote.DesiredCapabilities;
import java.util.logging.Logger;

public class DriverFactory {

    private static final ThreadLocal<WebDriver> driver = new ThreadLocal<>();
    private static final Logger logger = Logger.getLogger(DriverFactory.class.getName());

    public static WebDriver getDriver() {
        String browser = System.getProperty("browser"); // Maven parametresi kontrolü
        if (browser == null || browser.isEmpty()) {
            browser = System.getenv("BROWSER"); // Enviroment değişken kontrolü
        }
        browser = (browser == null || browser.isEmpty()) ? "CHROME" : browser.toUpperCase(); // Varsayılan tarayıcı

        switch (browser) {
            case "EDGE":
                WebDriverManager.edgedriver().setup();
                driver.set(new EdgeDriver());
                break;
            case "FIREFOX":
                WebDriverManager.firefoxdriver().setup();
                driver.set(new FirefoxDriver());
                break;
            case "CHROME":
                WebDriverManager.chromedriver().setup();
                driver.set(new ChromeDriver());
                break;
            case "MOBILE":
                ChromeOptions options = new ChromeOptions();
                options.setExperimentalOption("mobileEmulation", ImmutableMap.of("deviceName", "Samsung Galaxy S8+"));

                DesiredCapabilities capabilities = new DesiredCapabilities();
                capabilities.setCapability("platformName", "Android");
                capabilities.setCapability("deviceName", "Android Emulator");
                capabilities.setCapability("browserName", "Chrome");

                WebDriverManager.chromedriver().setup();
                driver.set(new ChromeDriver(options));
                logger.info("Mobile chrome driver baslatılması başarılı");
                break;
            default:
                throw new IllegalArgumentException("Geçersiz tarayici = " + browser);
        }

        driver.get().manage().window().maximize();
        return driver.get();
    }
}