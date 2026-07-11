# NexusERP - Çoklu Platform Üretim Takip ve Yönetim Sistemi

**NexusERP**, üretim süreçlerinin saha operasyonlarını, stok yönetimini ve sipariş sevk süreçlerini rol tabanlı olarak yöneten çoklu platform (Multi-Platform) bir uygulamadır. 

Proje; farklı çalışma ortamlarına sahip üç bağımsız istemcinin (Mobil, Masaüstü ve Backend), tek bir merkezi veritabanı ve iş mantığı katmanı etrafında senkronize şekilde çalışmasını hedefler.

---

## Sistem Mimarisi & Teknoloji Yığını

Sistem, servis odaklı bir mimariyle tasarlanmıştır. Tüm istemciler (Mobile & Desktop) veri işlemlerini ve kontrollerini merkezi bir REST API üzerinden gerçekleştirir.

```text
┌──────────────────────────────────┐      ┌──────────────────────────────────┐
│  Android Mobile Client (Kotlin)  │      │   Windows Desktop Client (C#)    │
│ [Jetpack Compose, Retrofit, UDF] │      │     [HttpClient, Async Engine]   │
└─────────────────┬────────────────┘      └─────────────────┬────────────────┘
                  │                                         │
                  │             (HTTP / REST)               │
                  └────────────────────┬────────────────────┘
                                       ▼
┌────────────────────────────────────────────────────────────────────────────┐
│                    Java Backend (Spring Boot REST API)                     │
│               [Merkezi Validasyon, Domain Logic, ORM Katmanı]              │
└──────────────────────────────────────┬─────────────────────────────────────┘
                                       │
                                       ▼
┌────────────────────────────────────────────────────────────────────────────┐
│                    Neon Tech Cloud PostgreSQL Database                     │
└────────────────────────────────────────────────────────────────────────────┘

```

---

## Özellikler

### Java API (Backend)

* **Merkezi Validasyon**: Stok düşümleri, sevk durumları ve üretim girişleri için gerekli tüm iş kuralları backend üzerinde yönetilir.
* **Veri İlişkileri ve Modelleme**: Üretim, ürün, sipariş ve kullanıcı domainleri arasındaki SQL ilişkileri Hibernate ORM katmanı ile modellenmiştir.

### Android Mobil Uygulama

* **Barkod ve QR Okuyucu**: Saha veri girişini hızlandırmak amacıyla, cihaz kamerası Google ML Kit ile entegre edilmiştir. Barkodlar çalışma zamanında taranarak API'ye gönderilir.
* **Tek Yönlü Veri Akışı (UDF)**: Jetpack Compose arayüzü, StateFlow mekanizmaları kullanılarak MVVM mimarisiyle beslenir.

### Windows Desktop Paneli

* **Asenkron Ağ İletişimi**: Yönetim panelinde, ana arayüzü kilitlemeden arka planda HTTP isteklerini yöneten asenkron bir iletişim katmanı yer alır.
* **Rol Tabanlı Dinamik Arayüz**: Giriş yapan personelin rolüne (Planlamacı, Sevkiyatçı vb.) göre ekranlar ve yetkiler dinamik olarak şekillenir.

### İş Akışları ve Süreç Yönetimi

* **Stok ve Sevkiyat Doğrulama**: Sipariş sevk edilirken sistem veritabanındaki stok miktarını kontrol eder. Stok yeterliyse otomatik düşüm yapılır, yeterli değilse işlem iptal edilerek hata mesajı dönülür.
* **Makine Durum Kayıtları**: Sistemdeki tanımlı makinelerin çalışma, durma veya arıza durumları loglanarak panellerde raporlanır.

---

## Kullanılan Teknolojiler

* **Spring Boot & Java 17**: Merkezi REST API servislerinin ve iş mantığının geliştirilmesinde kullanıldı.
* **PostgreSQL**: Sistem verilerinin bulut ortamında (Neon Tech) ilişkisel olarak saklanmasını sağladı.
* **Jetpack Compose & Kotlin**: Mobil uygulamanın modern arayüz bileşenleriyle geliştirilmesinde kullanıldı.
* **Google ML Kit**: Mobil cihaz kamerası üzerinden barkod/QR tarama işlemleri için entegre edildi.
* **C# & .NET Windows Forms**: İdari yönetim ve planlama panelinin masaüstünde çalışmasını sağladı.

---

## Öğrenilenler ve Deneyimler

* **Çoklu Platform Senkronizasyonu**: Android mobil uygulaması ile C# masaüstü uygulamasının aynı merkezi REST API ile asenkron ve tutarlı bir şekilde haberleşmesini sağlama deneyimi kazandım.
* **Donanım Entegrasyonu**: Google ML Kit kütüphanesini kullanarak mobil cihaz kamerasını bir barkod tarayıcı olarak uygulamaya entegre etmeyi öğrendim.
* **Merkezi İş Mantığı Yönetimi**: İş kurallarını istemci tarafında dağıtmak yerine tamamen backend katmanında toplayarak daha güvenli ve tutarlı bir sistem tasarlamayı uyguladım.

---

## Mobil Görseller

| | |
| :---: | :---: |
| <img src="screenshots/mobile_operator.jpg" width="300"/> | <img src="screenshots/mobile_planner_home.jpg" width="300"/> |
| <img src="screenshots/mobile_stock.jpg" width="300"/> | <img src="screenshots/mobile_settings.jpg" width="300"/> |

---

## Masaüstü Görselleri

| | |
| :---: | :---: |
| <img src="screenshots/desktop_planner_products.jpg" width="450"/> | <img src="screenshots/desktop_planner_dash.jpg" width="450"/> |

---

## Demo (GIF)

![NexusERP](screenshots/output.gif)

---

## Nasıl Çalıştırılır?

### 1. Sunucu Katmanı

```bash
cd backend
mvn clean install
mvn spring-boot:run

```

### 2. Masaüstü Katmanı

* `client-desktop/NexusERP.slnx` çözüm dosyasını Visual Studio veya Rider ile açın.


* Bağımlılıkların yüklenmesinin ardından projeyi derleyip çalıştırın.



### 3. Mobil Katman

* `client-mobile/NexusERP` projesini Android Studio ile içeri aktarın.


* Gradle senkronizasyonunun ardından emülatör veya gerçek cihazda çalıştırın.
