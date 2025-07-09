package driver;

import com.thoughtworks.gauge.AfterScenario;
import com.thoughtworks.gauge.BeforeScenario;
import org.openqa.selenium.WebDriver;

public class Driver {

    public static WebDriver webDriver;

    @BeforeScenario
    public void initializeDriver() {
        webDriver = DriverFactory.getDriver();
    }

    @AfterScenario
    public void closeDriver() {
        if (webDriver != null) {
            webDriver.quit();
        }
    }
}