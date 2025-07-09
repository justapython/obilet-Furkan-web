package org.example;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.thoughtworks.gauge.Step;
import driver.Driver;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.FileReader;
import java.io.IOException;
import java.time.Duration;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.NoSuchElementException;

import static org.assertj.core.api.Assertions.assertThat;

public class StepImplementation {

    /**
     * Json dosyasından element döndürme için kullanılan method.
     */
    private By getBy(String key) {
        try {
            // Dosya (json) okuma
            JsonObject jsonObject = JsonParser.parseReader(new FileReader("src/test/resources/elements.json")).getAsJsonObject();
            if (!jsonObject.has(key)) {
                throw new IllegalArgumentException("Json dosyasında element bulunamadı element = " + key);
            }

            JsonObject elementData = jsonObject.getAsJsonObject(key);
            String locatorType = elementData.get("locatorType").getAsString();
            String value = elementData.get("value").getAsString();

            // Elemente göre by objesi olusutrma islemi
            switch (locatorType.toLowerCase()) {
                case "id":
                    return By.id(value);
                case "linktext":
                    return By.linkText(value);
                case "css":
                    return By.cssSelector(value);
                case "xpath":
                    return By.xpath(value);
                default:
                    throw new IllegalArgumentException("Geçersiz bir locator tipi = : " + locatorType);
            }
        } catch (IOException e) {
            throw new RuntimeException("Json dosyasi okumasi basarisiz oldu = " + e.getMessage());
        }
    }

    @Step("URL adresine gidilir <url>")
    public void goToURL(String url) {
        Driver.webDriver.get(url); // Url parametresi alinir
        try {
            assertThat(Driver.webDriver.getCurrentUrl()).isEqualTo(url);
            System.out.println("Yönlendirme yapilan URL = " + url + " successfully.");
        } catch (AssertionError e) {
            System.out.println("Yönlendirme yapilamadi URL = " + url);
            throw e;
        }
    }

    @Step("<key> elementinin tıklanabilir olduğu kontrol edilir <timeout>")
    public void waitUntilElementClickable(String key, int timeout) {
        // element tıklanabilir mi diye kontrol saglanir. Maksimum timeout belirlenir.
        By locator = getBy(key);
        WebDriverWait wait = new WebDriverWait(Driver.webDriver, Duration.ofSeconds(timeout));
        try {
            WebElement element = wait.until(ExpectedConditions.elementToBeClickable(locator));
            assertThat(element).isNotNull();
            System.out.println("Element " + key + " tiklanabilir durumda.");
        } catch (AssertionError e) {
            System.out.println("Element " + key + " tiklanabilir durumda degil, beklenen sure = " + timeout + " saniye.");
            throw e;
        }
    }

    @Step("<key> elementine tıklanır")
    public void clickElement(String key) {
        By locator = getBy(key);
        WebElement element = Driver.webDriver.findElement(locator);
        try {
            assertThat(element.isDisplayed()).isTrue();
            element.click();
            System.out.println("Elemente basariyla tiklandi. Key degeri= " + key);
        } catch (AssertionError e) {
            System.out.println("Elemente tiklama basarisiz oldu. Key degeri= " + key);
            throw e;
        }
    }

    @Step("<key> elementinin text değeri <text> değerini içeriyor mu kontrol et")
    public void isElementTextContains(String key, String text) {
        By locator = getBy(key);

        try {
            // Dinamik bekleme - max 15 saniye
            WebDriverWait wait = new WebDriverWait(Driver.webDriver, Duration.ofSeconds(15));
            WebElement element = wait.until(ExpectedConditions.visibilityOfElementLocated(locator));

            // Gelen metnin icindeki bosluk ve satır sonları düzenlerir
            String elementText = element.getText().replaceAll("\\s+", " ").trim();
            String expected = text.replaceAll("\\s+", " ").trim();

            assertThat(elementText).contains(expected);
            System.out.println("Text dogrulandi. Element icerigi: " + elementText);

        } catch (Exception e) {
            System.out.println("Text dogrulamasi basarisiz. Beklenen: " + text);
            throw e;
        }
    }

    @Step("Uygun koltuklardan rastgele bir tanesi seçilir ve tıklanır")
    public void selectAndClickRandomAvailableSeat() {
        List<WebElement> allSelectableSeats = new ArrayList<>();

        // Tekli koltuklarda avilable'ın all oldukları listeye alınır
        List<WebElement> singleSeats = Driver.webDriver.findElements(
                By.cssSelector("a.available.active.single-seat.ins-init-condition-tracking[obilet\\:available='all']")
        );

        // Çiftli koltuklarda avilable'ın Male olanları listeye alınır (Casede erkek yolcu ile ilerledim)
        List<WebElement> doubleSeats = Driver.webDriver.findElements(
                By.cssSelector("a.available.active.not-single-seat.ins-init-condition-tracking[obilet\\:available='M']")
        );

        allSelectableSeats.addAll(singleSeats);
        allSelectableSeats.addAll(doubleSeats);

        if (allSelectableSeats.isEmpty()) {
            throw new NoSuchElementException("Uygun koltuk bulunamadı.");
        }

        // İki liste birlestirildi ve icerisinden bir random koltuk secildi
        Random random = new Random();
        WebElement randomSeat = allSelectableSeats.get(random.nextInt(allSelectableSeats.size()));

        try {
            randomSeat.click();
            System.out.println("Rastgele koltuk basariyla secildi ve tiklandi.");
        } catch (Exception e) {
            System.out.println("Koltuk tıklanamadı.");
            throw e;
        }
        waitUntilElementClickable("SeferlerPageGenderMaleButton", 10);
        clickElement("SeferlerPageGenderMaleButton");

        // Onlayla ve devam butonunun oldugu sayfada firma adı ve fiyat bilgileri alındı
        String firmaAdiIlkSayfa = Driver.webDriver.findElement(
                By.xpath("(//div[@class='partner-logo'][@data-name])[1]")
        ).getAttribute("data-name");
        System.out.println("Ilk sayfadaki firma adi: " + firmaAdiIlkSayfa);

        String fiyatIlkSayfa = Driver.webDriver.findElement(
                By.xpath("(//span[@class='amount-integer'])[2]")
        ).getText().trim();
        System.out.println("Ilk sayfadaki fiyat: " + fiyatIlkSayfa);

        clickElement("SeferlerPageOnaylaVeDevamButton");
        waitUntilElementClickable("OdemePageOdemeYapButton", 25);

        // Onlayla ve devam butonunundan sonra gelen odeme sayfasında firma adı ve fiyat bilgileri alındı
        String firmaAdiIkinciSayfa = Driver.webDriver.findElement(
                By.xpath("(//img[@alt])[2]")
        ).getAttribute("alt");
        System.out.println("Ikinci sayfadaki firma adi: " + firmaAdiIkinciSayfa);

        // Text değeri burası icin javascript ile alındı
        String fiyatIkinciSayfa = (String) ((JavascriptExecutor) Driver.webDriver)
                .executeScript("return document.evaluate(\"//div[@id='amount']/text()[1]\", document, null, XPathResult.STRING_TYPE, null).stringValue;");

    // Bosluklar temizleniyor esitlik kontrolu icin
        fiyatIkinciSayfa = fiyatIkinciSayfa.replace("\u00A0", "") // non-breaking space
                .replaceAll("[\\s\\u00A0]+", "") // tüm boşluk karakterleri
                .trim();

        System.out.println("Ikinci sayfadaki fiyat: " + fiyatIkinciSayfa);


        // assertionlar ile beraber iki sayfa arasındaki degerler karsılastırıldı
        assertThat(firmaAdiIkinciSayfa)
                .withFailMessage("Firma adlari ayni degil, ilk sayfa: %s, ikinci sayfa: %s", firmaAdiIlkSayfa, firmaAdiIkinciSayfa)
                .isEqualTo(firmaAdiIlkSayfa);

        assertThat(fiyatIkinciSayfa)
                .withFailMessage("Fiyat bilgileri ayni degil, ilk sayfa: %s, ikinci sayfa: %s", fiyatIlkSayfa, fiyatIkinciSayfa)
                .isEqualTo(fiyatIlkSayfa);

    }

    @Step("Bugünden <key> gün sonrasındaki tarihe tıklanır")
    public void clickDatePlusDays(int key) {
        // Bugün + <key> gün tarihini al
        LocalDate today = LocalDate.now();
        LocalDate targetDate = today.plusDays(key);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        String dateStr = targetDate.format(formatter);

        // Element olusturulur ve tarih secilir
        By locator = getBy2(dateStr);
        Driver.webDriver.findElement(locator).click();
    }

    public By getBy2(String key) {
        // tarih formati icin ozel element duzenleme methodu
        if (key.matches("\\d{4}-\\d{2}-\\d{2}")) {
            return By.cssSelector("button[data-date='" + key + "']");
        }
        return null;
    }

    @Step("<key> arama kutusuna <text> yazılır ve seçim yapılır")
    public void searchAndSelect(String key, String text) {
        By locator = getBy(key);
        WebDriverWait wait = new WebDriverWait(Driver.webDriver, Duration.ofSeconds(10));

        WebElement input = wait.until(ExpectedConditions.elementToBeClickable(locator));
        clickElement(key);
        input.sendKeys(text);

        // Dropdown'un dolması beklenirken statik bekleme kullanıldı
        try {
            Thread.sleep(2200);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        input.sendKeys(Keys.ENTER);
        input.sendKeys(Keys.ENTER);
    }

    @Step("Random cıkabilecek olan banner bypass edilir")
    public void handleRandomPopup() {
        try {
            WebDriverWait shortWait = new WebDriverWait(Driver.webDriver, Duration.ofSeconds(2));
            WebElement closeBtn = shortWait.until(ExpectedConditions
                    .elementToBeClickable(By.id("wrap-close-button-1454703513200")));
            closeBtn.click();
            System.out.println("Banner basari ile bypass edildi");
        } catch (TimeoutException e) {
            System.out.println("Engel olacak bir banner goruntulenmedi");
        }
    }
    @Step("<key> elementine scroll edilir")
    public void scrollToElement(String key) {
        By locator = getBy(key);
        try {
            WebElement element = Driver.webDriver.findElement(locator);
            ((JavascriptExecutor) Driver.webDriver).executeScript("arguments[0].scrollIntoView({behavior: 'smooth', block: 'center'});", element);
            System.out.println(key + " elementine scroll islemi basarili.");
        } catch (Exception e) {
            System.out.println(key + " elementine scroll edilirken hata olustu: " + e.getMessage());
            throw e;
        }
    }

    @Step("Bugünden <key> gün sonrası giriş tarihi <key2> gün sonrası çıkış tarihi olarak seçilir")
    public void selectEntryAndExitDates(int key, int key2) {
        // Sistem tarihi olarak bugün + key gün sonrası giriş tarihi belirlenir
        LocalDate today = LocalDate.now();
        LocalDate entryDate = today.plusDays(key);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        String entryDateStr = entryDate.format(formatter);

        By entryLocator = getBy2(entryDateStr);
        Driver.webDriver.findElement(entryLocator).click();

        // statik bekleme ikinci tarih seciminden once kullanıldı, otomasyonun cok hızlı gecmemesi icin
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Sistem tarihi olarak bugün + key2 gün sonrası cıkış tarihi belirlenir
        LocalDate exitDate = today.plusDays(key2);
        String exitDateStr = exitDate.format(formatter);

        By exitLocator = getBy2(exitDateStr);
        Driver.webDriver.findElement(exitLocator).click();
    }

    @Step("Otel fiyatlarının küçükten büyüğe sıralandığı doğrulanır")
    public void verifyHotelPricesAreSortedAscending() {
        waitUntilElementClickable("OtelListPageOdaSecButton",10);
        List<WebElement> priceElements = Driver.webDriver.findElements(
                By.xpath("//div[@class='hotel-price__amount']")
        );

        if (priceElements.isEmpty()) {
            throw new NoSuchElementException("Fiyat listesi alinamadi, //div[@class='hotel-price__amount'] elementi bulunamadı.");
        }

        List<Integer> actualPrices = new ArrayList<>();

        for (WebElement priceElement : priceElements) {
            String rawText = priceElement.getText().trim();
            // element değeri icerisindeki TL . ve bosluklar replace ile kaldırılır
            String numericText = rawText
                    .replace("TL", "")
                    .replace(".", "")
                    .replace("\u00A0", "")
                    .trim();

            try {
                int price = Integer.parseInt(numericText); // int dönüsümü karsılalastırma icin gerceklestirilir
                actualPrices.add(price);
            } catch (NumberFormatException e) {
                System.out.println("(Olası donusum problemleri icin yazildi) ---> Hata : " + rawText + " → " + numericText);
                throw e;
            }
        }

        // Int gelen fiyat değerleri sort ile kucukten buyuge siralanir
        List<Integer> sortedPrices = new ArrayList<>(actualPrices);
        Collections.sort(sortedPrices);

        assertThat(actualPrices)
                .withFailMessage("Fiyatlar küçükten büyüğe sıralı değil, Sıralama: %s", actualPrices)
                .isEqualTo(sortedPrices);

        System.out.println("Tum otel fiyatlari kucukten buyuge siralanmis bicimde, degerler: " + actualPrices);
    }

}

