# Genie Web Otomasyon Testleri

Java 17, Selenium ve JUnit ile hazırlanmış üç web testi. Sayfa işlemleri `pages`, senaryolar ve sonuç kontrolleri `tests` paketindedir.

## Gereksinimler

- JDK 17
- IntelliJ IDEA
- Google Chrome ve internet bağlantısı

Maven bağımlılıkları ilk açılışta indirilir. Selenium Manager uygun ChromeDriver sürümünü otomatik bulur veya indirir.

## IntelliJ ile çalıştırma

1. ZIP'i çıkar. **File → Open** ile `pom.xml` dosyasının bulunduğu `genie-selenium-final` klasörünü aç.
2. Gerekirse **Trust Project** seç. Maven bildirimi çıkarsa **Load** ile yükle.
3. Project SDK olarak **JDK 17** seç. Maven yüklemesi tamamlanana kadar bekle.
4. `src/test/java/tests/GenieChatTest.java` dosyasını aç.
5. Sınıf adının yanındaki yeşil ok → **Run GenieChatTest** ile üç testi çalıştır.

Tek bir metot yanındaki ok yalnızca o testi çalıştırır. Maven kuruluysa proje klasöründe `mvn test` komutu da kullanılabilir.

## Proje yapısı

```
src/test/java/
  pages/
    ChatPage.java
    LoginPopup.java
    PageActions.java
  tests/
    GenieChatTest.java
```

- `ChatPage`: Mesaj alanı, gönderme, tema değişimi ve chat ekranı durumları.
- `LoginPopup`: Giriş seçenekleri, e-posta doğrulaması ve pencereyi kapatma.
- `PageActions`: Öğeyi bulma ve işlemi aynı bekleme içinde yapar. Yalnızca eskiyen öğe ve belirtilen ChromeDriver node hatasında tekrar dener; diğer hataları gizlemez.
- `GenieChatTest`: Tarayıcı açma/kapatma, senaryo adımları ve assert kontrolleri.

Her test yeni bir geçici Chrome oturumunda çalışır. Testler birbirine ve çalıştırma sırasına bağlı değildir. Türkçe yorumlar senaryo adımlarını ve özel bekleme durumlarını açıklar.

## Kapsam

| Test | Kontroller |
|---|---|
| guestMessageOpensLogin | Karşılama ekranı, boş geçmiş ve Login butonu; mesaj sonrası login penceresi ve giriş seçenekleri; pencere kapanınca taslağın korunması. |
| invalidEmailShowsError | `abc` için “Email must be a valid email address” mesajı, `aria-invalid=true` ve login penceresinin açık kalması. |
| themeChangesAndKeepsDraft | Temanın iki yönde değişmesi ve iki geçişte de taslağın korunması. |

Gerçek giriş ve AI yanıt kalitesi test edilmez. Sohbetin başlamadığı karşılama ekranı ve boş History üzerinden kontrol edilir; doğrudan cevap balonu kontrolü yapılmaz. Tema HTML'nin light/dark sınıfından doğrulanır; görsel kontrast manuel inceleme gerektirir. New Chat ve sayfa yenileme kapsam dışındadır.

## Sonuçlar

Sonuçlar IntelliJ test panelinde görünür. Her test sonunda `target/<test-metodu>.png` ekran görüntüsü kaydedilir. Aynı test yeniden çalıştırıldığında önceki görüntünün üzerine yazılır. Ekran görüntüsü tek başına başarı kanıtı değildir.

Beklemeler en fazla 30 saniyedir. Görünürlük, okuma, yazma ve tıklama işlemleri ortak PageActions sınıfında gerçekleştirilir. Her denemede öğe yeniden bulunur. Yalnızca StaleElementReferenceException ve “Node with given id does not belong to the document” hatasında yeniden denenir. Diğer hatalar testi başarısız bırakır. Yazma tekrarlarında alan temizlenerek metnin iki kez eklenmesi önlenir.

Görünmesi beklenen öğeler için en fazla 30 saniye beklenir. Görünürlük kontrolleri süre dolarsa false döndürür; açıklamalı assert mesajları test sonucunda gösterilir. Başlangıçta mesaj alanının boşluğu doğrulanır. Boş alanda butonun gizli olduğu varsayılmaz; mesaj yazıldıktan sonra gönder butonunun görünürlüğü ve tıklanabilirliği kontrol edilir. Kapatılan pencere için anlık kontrol yapılır.

Mesaj yazarken alanın düzenlenebilirliği ve odağı kontrol edilir. ElementNotInteractableException yalnızca yazma işlemi içinde tekrar denenir; kalıcı sorun 30 saniye sonunda testin başarısız olmasına yol açar.
