package pages;

import org.openqa.selenium.*;

public class LoginPopup {
    private final PageActions actions;
    private final By dialog = By.cssSelector("[role='dialog'][data-slot='dialog-content']");
    private final By email = By.id("login-page-email-input");
    private final By emailButton = By.id("mail-sign-in-button");

    public LoginPopup(WebDriver driver) { actions = new PageActions(driver); }

    public void waitUntilOpen() {
        if (!actions.visible(dialog)) {
            throw new TimeoutException("Login penceresi 30 saniye içinde açılmadı");
        }
        actions.until(d -> d.getCurrentUrl().contains("login=true"));
    }

    public String title() {
        return actions.text(By.cssSelector("[data-slot='dialog-content'] h1"));
    }

    public boolean providerVisible(String provider) {
        return actions.visible(By.cssSelector("[data-slot='dialog-content'] #" + provider + "-sign-in-button"));
    }

    public boolean emailVisible() { return actions.visible(email); }
    public String emailButtonText() { return actions.text(emailButton); }

    // Geçersiz e-posta gönderildiğinde hata metni test sınıfında doğrulanır.
    public void submitEmail(String value) {
        actions.write(email, value);
        actions.click(emailButton);
    }

    public String errorText() {
        return actions.text(By.cssSelector("[data-slot='dialog-content'] [data-slot='form-message']"));
    }

    public String emailInvalid() { return actions.attribute(email, "aria-invalid"); }

    public boolean visible() {
        return actions.visibleNow(dialog);
    }

    // X ile kapat ve pencerenin kaybolmasını bekle.
    public void close() {
        actions.click(By.cssSelector("[data-slot='dialog-content'] > button[type='button']"));
        actions.until(d -> !actions.visibleNow(dialog));
    }
}
