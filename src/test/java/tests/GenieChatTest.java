package tests;

import org.junit.jupiter.api.*;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import pages.ChatPage;
import pages.LoginPopup;
import java.nio.file.*;
import static org.junit.jupiter.api.Assertions.*;

public class GenieChatTest {
    private WebDriver driver;
    private ChatPage chat;
    private LoginPopup login;

    // Her testten önce yeni Chrome aç, siteye git ve çerezleri kabul et.
    @BeforeEach
    void openSite() {
        driver = new ChromeDriver();
        driver.manage().window().maximize();
        chat = new ChatPage(driver);
        login = new LoginPopup(driver);
        chat.open();
    }

    // Her test sonunda ekran görüntüsü kaydet ve tarayıcıyı kapat.
    @AfterEach
    void closeBrowser(TestInfo testInfo) {
        if (driver == null) return;
        try {
            Path image = Path.of("target", testInfo.getTestMethod().orElseThrow().getName() + ".png");
            Files.createDirectories(image.getParent());
            Files.write(image, ((TakesScreenshot) driver).getScreenshotAs(OutputType.BYTES));
        } catch (Exception error) {
            System.err.println("Ekran görüntüsü alınamadı: " + error.getMessage());
        } finally {
            driver.quit();
        }
    }

    @Test
    void guestMessageOpensLogin() {
        String question = "Türkiye'nin Başkenti Neresidir?";
        // 1. Karşılama ekranını, boş geçmişi ve Login butonunu doğrula.
        assertTrue(chat.welcomeVisible(), "Karşılama yazısı görünmeli");
        assertTrue(chat.historyEmpty(), "Sohbet geçmişi boş olmalı");
        assertTrue(chat.loginButtonVisible(), "Login butonu görünmeli");
        // 2. Yeni oturumda mesaj taslağının boş olduğunu doğrula.
        assertEquals("", chat.draftText().trim(), "Yeni oturumda mesaj alanı boş olmalı");

        // 3. Soruyu yaz, gönder butonunun göründüğünü doğrula ve gönder.
        chat.typeMessage(question);
        assertTrue(chat.waitForSendVisible(), "Mesaj yazılınca gönder butonu görünmeli");
        chat.sendMessage();
        login.waitUntilOpen();
        // 4. Login yönlendirmesini, başlığı ve giriş seçeneklerini doğrula.
        assertTrue(driver.getCurrentUrl().contains("redirect=/"), "Login yönlendirmesi bulunmalı");
        assertEquals("Welcome to chatbotai.com", login.title());
        for (String provider : new String[]{"google", "facebook", "apple", "microsoft", "mail"}) {
            assertTrue(login.providerVisible(provider), provider + " butonu görünmeli");
        }
        assertTrue(login.emailVisible(), "E-posta alanı görünmeli");
        assertEquals("Continue with Email", login.emailButtonText());

        // 5. Pencereyi kapat; boş geçmişi, taslağı ve mesaj alanını kontrol et.
        login.close();
        assertFalse(login.visible(), "Login penceresi kapanmalı");
        assertTrue(chat.welcomeVisible(), "Karşılama yazısı görünmeli");
        assertTrue(chat.historyEmpty(), "Sohbet geçmişi boş olmalı");
        assertEquals(question, chat.draftText(), "Taslak korunmalı");
        assertTrue(chat.editorUsable(), "Mesaj alanı kullanılabilir olmalı");
    }

    @Test
    void invalidEmailShowsError() {
        // 1. Misafir olarak mesaj gönder ve login penceresini aç.
        chat.typeMessage("Merhaba");
        chat.sendMessage();
        login.waitUntilOpen();
        // 2. Geçersiz e-posta gir ve Continue with Email butonuna bas.
        login.submitEmail("abc");
        // 3. Hata metnini, geçersiz alanı ve pencerenin açık kaldığını doğrula.
        assertEquals("Email must be a valid email address", login.errorText());
        assertEquals("true", login.emailInvalid());
        assertTrue(login.visible(), "Login penceresi açık kalmalı");
    }

    @Test
    void themeChangesAndKeepsDraft() {
        // 1. Taslak mesaj yaz ve başlangıç temasını öğren.
        String draft = "Tema testi için taslak";
        chat.typeMessage(draft);
        String original = chat.theme();
        String other = original.equals("dark") ? "light" : "dark";

        // 2. Diğer temaya geç; temayı ve taslağın korunduğunu doğrula.
        chat.changeThemeTo(other);
        assertEquals(other, chat.theme());
        assertEquals(draft, chat.draftText());
        // 3. Başlangıç temasına dön ve taslağı tekrar kontrol et.
        chat.changeThemeTo(original);
        assertEquals(original, chat.theme());
        assertEquals(draft, chat.draftText());
    }
}
