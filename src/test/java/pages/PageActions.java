package pages;

import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.WebDriverWait;
import java.time.Duration;
import java.util.function.Function;

// Sayfa yenilendiğinde öğeyi yeniden bulur; yalnızca iki geçici hata tekrar denenir.
public class PageActions {
    private final WebDriver driver;
    private final WebDriverWait wait;

    public PageActions(WebDriver driver) {
        this.driver = driver;
        wait = new WebDriverWait(driver, Duration.ofSeconds(30));
    }

    public <T> T until(Function<WebDriver, T> condition) {
        return wait.until(d -> {
            try {
                return condition.apply(d);
            } catch (StaleElementReferenceException error) {
                return null;
            } catch (WebDriverException error) {
                if (error.getMessage() != null && error.getMessage().contains(
                        "Node with given id does not belong to the document")) {
                    return null;
                }
                throw error;
            }
        });
    }

    public boolean visibleNow(By locator) {
        for (WebElement element : driver.findElements(locator)) {
            if (element.isDisplayed()) return true;
        }
        return false;
    }

    public boolean visible(By locator) {
        try {
            return until(d -> visibleNow(locator));
        } catch (TimeoutException error) {
            return false;
        }
    }

    // Görünür ve etkin olan ilk öğeyi seç; gizli mobil öğeyi atla.
    private WebElement usableElement(By locator) {
        for (WebElement element : driver.findElements(locator)) {
            if (element.isDisplayed() && element.isEnabled()) return element;
        }
        return null;
    }

    public void click(By locator) {
        until(d -> {
            WebElement element = usableElement(locator);
            if (element == null) return false;
            element.click();
            return true;
        });
    }

    public void write(By locator, String text) {
        until(d -> {
            WebElement element = usableElement(locator);
            if (element == null || !editable(element)) return false;
            try {
                element.click();
                if (!element.equals(d.switchTo().activeElement())) return false;
                replaceText(element, text);
                return text.equals(readInput(element));
            } catch (ElementNotInteractableException error) {
                return false;
            }
        });
    }

    // Chat bir contenteditable div; e-posta ise normal input alanıdır.
    private boolean editable(WebElement element) {
        if (element.getDomAttribute("readonly") != null) return false;
        if (!"div".equalsIgnoreCase(element.getTagName())) return true;
        return "true".equals(element.getDomAttribute("contenteditable"));
    }

    // Yeniden denemede metin iki kez eklenmesin: seç, sil, yaz.
    private void replaceText(WebElement element, String text) {
        Keys key = System.getProperty("os.name").startsWith("Mac") ? Keys.COMMAND : Keys.CONTROL;
        element.sendKeys(Keys.chord(key, "a"), Keys.BACK_SPACE, text);
    }

    private String readInput(WebElement element) {
        String value = element.getDomProperty("value");
        return value == null ? element.getText() : value;
    }

    public String text(By locator) {
        return until(d -> {
            for (WebElement element : d.findElements(locator)) {
                if (element.isDisplayed()) return element.getText();
            }
            return null;
        });
    }

    public String attribute(By locator, String name) {
        return until(d -> d.findElement(locator).getDomAttribute(name));
    }
}
