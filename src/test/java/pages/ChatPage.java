package pages;

import org.openqa.selenium.*;

public class ChatPage {
    private final WebDriver driver;
    private final PageActions actions;
    private final By editor = By.id("chat-input-textarea");
    private final By send = By.id("chat-input-send-button");
    private final By welcome = By.xpath("//*[normalize-space()='Welcome to chatbotai.com - your personal AI assistant']");
    private final By history = By.xpath("//p[normalize-space()='No chat history']");

    public ChatPage(WebDriver driver) {
        this.driver = driver;
        actions = new PageActions(driver);
    }

    // Siteyi aç ve çerez penceresinin kapanmasını bekle.
    public void open() {
        driver.get("https://chatbotai.com/");
        actions.click(By.id("cookiescript_accept"));
        actions.until(d -> !actions.visibleNow(By.id("cookiescript_accept")));
        if (!actions.visible(editor)) {
            throw new TimeoutException("Mesaj alanı 30 saniye içinde görünmedi");
        }
    }

    public boolean welcomeVisible() { return actions.visible(welcome); }
    public boolean historyEmpty() { return actions.visible(history); }

    // Gizli mobil buton yerine görünür Login butonunu kontrol et.
    public boolean loginButtonVisible() {
        return actions.visible(By.xpath("//button[normalize-space()='Login']"));
    }

    public boolean sendVisible() {
        return actions.visibleNow(send);
    }

    // Mesaj yazıldıktan sonra butonun çizilmesini bekle.
    public boolean waitForSendVisible() { return actions.visible(send); }

    // Yenilenen alana tekrar yazarken önceki metni temizleyerek tekrarı önle.
    public void typeMessage(String text) { actions.write(editor, text); }
    public void sendMessage() { actions.click(send); }
    public String draftText() { return actions.text(editor); }

    public boolean editorUsable() {
        return actions.until(d -> {
            WebElement input = d.findElement(editor);
            return input.isDisplayed() && input.isEnabled();
        });
    }

    // HTML sınıfından mevcut temayı oku.
    public String theme() {
        return actions.until(d -> {
            String classes = d.findElement(By.tagName("html")).getDomAttribute("class");
            String names = " " + classes + " ";
            if (names.contains(" dark ")) return "dark";
            if (names.contains(" light ")) return "light";
            return null;
        });
    }

    public void changeThemeTo(String expected) {
        actions.click(By.id("chat-sidebar-toggle-theme-button"));
        actions.until(d -> (" " + d.findElement(By.tagName("html")).getDomAttribute("class") + " ")
                .contains(" " + expected + " "));
    }
}
