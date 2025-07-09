# Obilet Web Automation Project

## Table of Contents

- [About the Project](#about-the-project)
- [Technologies Used](#technologies-used)
- [Features](#features)
- [Contact](#contact)

---

### About the Project

Bu proje, Selenium4 kullanılarak Obilet web sitesi üzerinde otomasyon testleri gerçekleştirmek amacıyla geliştirilmiştir. Proje kapsamında aşağıdaki işlevler test edilmektedir:

- **Dinamik Tarih Seçimi:** Günlerin dinamik olarak değiştirilebildiği ve sistemin bu değişikliklere doğru şekilde tepki verdiği kontrol edilir.
- **Koltuk Seçimi:** Boş koltuklardan yalnızca uygun olanların rastgele seçilmesi sağlanır.
- **Ödeme Adımı Doğrulaması:** Ödeme adımına gelindiğinde, önceki adımlardaki fiyat ve otobüs firması isimlerinin tutarlılığı kontrol edilir.
- **Otel Listeleme:** Dinamik olarak şehir ve tarih değişiklikleri yapılabilir ve fiyatların küçükten büyüğe doğru sıralanıp sıralanmadığı fonksiyonel olarak test edilir.
- **Loglama ve Debug:** Otomasyon sürecinde gerekli loglamalar ve debug işlemleri gerçekleştirilerek sorunların kolayca tespit edilmesi sağlanır.

Proje, kullanıcı dostu bir deneyim sunarken, sistemin kararlılığını ve doğruluğunu da garanti altına almayı hedeflemektedir.

---

### Technologies Used

- **Selenium4:** Web otomasyon testleri için kullanılan temel araç.
- **Maven:** Proje yönetimi ve bağımlılıkların yönetimi için.
- **Gauge:** Test senaryolarının kolayca yazılabilmesi ve yönetilebilmesi için.
- **Java:** Programlama dili olarak kullanılmıştır.
- **AssertJ:** Test doğrulamaları için kullanılan kütüphane.
- **Bonigarcia WebDriverManager /w Selenium4:** WebDriver'ların otomatik yönetimi için.
- **Json Formater - Gson:** JSON verilerinin işlenmesi ve formatlanması için.

---

### Features

- **Dinamik Bekleme Süreleri:** Web elementlerinin yüklenmesi için akıllı bekleme stratejileri kullanılır.
- **JSON'dan Element Okunabilirliği:** Test verileri ve element locator'ları JSON dosyalarından okunabilir, yönetilebilir hale getirilmiştir.
- **Spec Dosyası Üzerinden Kolay Değişken Atamaları:** Test senaryoları, spec dosyaları üzerinden kolayca özelleştirilebilir.
- **Page Object Model ile Okunabilir Tasarım:** Kod okunabilirliği ve sürdürülebilirlik için Page Object Model (POM) tasarım deseni kullanılmıştır.
- **Otobüs Bileti Doğrulaması:** Otobüs bileti alım sürecinde, fiyat ve firma bilgilerinin tutarlılığı otomatik olarak kontrol edilir.
- **Otel Fiyat Kontrolü:** Otel listeleme sayfalarında, fiyatların küçükten büyüğe doğru sıralanıp sıralanmadığı fonksiyonel olarak test edilir.

---

### Contact

- **Mail:** [furkanbicer803@gmail.com](mailto:furkanbicer803@gmail.com)
