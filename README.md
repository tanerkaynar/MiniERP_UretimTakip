# 🏢 NexusERP - Çoklu Platform Üretim Takip ve Yönetim Ekosistemi

**NexusERP**; üretim yapan bir işletmenin saha operasyonlarını, stok takibini, sipariş sevk süreçlerini ve makine verimlilik analizlerini yöneten, **3 katmanlı (Multi-Platform)** ve rol tabanlı çalışan bir üretim takip sistemidir.

Proje; bağımsız bir **Java REST API**, planlama ve idari yönetim için bir **Windows Desktop Client** ve saha operatörleri için donanım entegrasyonlu modern bir **Android Mobile Client** olmak üzere üç ana ayaktan oluşmaktadır. Hem mobil hem de masaüstü istemciler tüm işlemleri ortak bir REST API üzerinden gerçekleştirir.

---

## 🏗️ Sistem Mimarisi & Teknoloji Yığını

```text
┌──────────────────────────────────┐      ┌──────────────────────────────────┐
│  Android Mobile Client (Kotlin)  │      │   Windows Desktop Client (C#)    │
│ [Jetpack Compose, Retrofit, UDF]  │      │     [HttpClient, ThemeHelper]    │
└─────────────────┬────────────────┘      └─────────────────┬────────────────┘
                  │                                         │
                  │              (HTTP / REST)              │
                  └────────────────────┬────────────────────┘
                                       ▼
┌────────────────────────────────────────────────────────────────────────────┐
│                    Java Backend (Spring Boot REST API)                     │
│         [Java 17, Spring Boot, Spring Data JPA, Hibernate, PostgreSQL]     │
└──────────────────────────────────────┬─────────────────────────────────────┘
                                       │
                                       ▼
┌────────────────────────────────────────────────────────────────────────────┐
│                    Neon Tech Cloud PostgreSQL Database                     │
└────────────────────────────────────────────────────────────────────────────┘
```

### 1. Java Backend (REST API)
- **Teknolojiler**: Java 17, Spring Boot, Spring Data JPA, Hibernate.
- **Veritabanı**: PostgreSQL (Neon Tech Cloud entegrasyonu).
- **Veri Tohumlama**: `DataInitializer` sınıfı ile uygulama ilk kez çalıştığında veya veritabanı sıfırlandığında varsayılan `admin` hesabının otomatik olarak oluşturulması.
- **Veri Bütünlüğü**: Sipariş ve üretim işlemlerinde `@Transactional` blokları ile güvenli veri kaydı.

### 2. Windows Desktop Client (C#)
- **Teknolojiler**: .NET Framework 4.7.2, Windows Forms, HttpClient, Newtonsoft.Json.
- **Ağ İletişimi**: Merkez Spring Boot API ile asenkron veri senkronizasyonu sağlayan `ApiClient` sarmalayıcısı.
- **Arayüz ve Tema**: Açık ve koyu tema desteği sunan özelleştirilmiş `ThemeHelper` arayüz yapısı.
- **Veri Analitiği**: Makine bazlı üretim raporlarının çizgi ve sütun grafiklerle görselleştirilmesi.

### 3. Mobile Client (Android)
- **Teknolojiler**: Modern Kotlin & Jetpack Compose (Declarative UI).
- **Mimari**: MVVM mimarisi ve StateFlow ile asenkron veri akış yönetimi.
- **Donanım Entegrasyonu**: Google ML Kit entegrasyonlu kamera tabanlı barkod okuyucu sayesinde hızlı üretim kaydı girişi.

---

## 🌟 Öne Çıkan Özellikler

### 🔐 Rol Tabanlı Dinamik Yetkilendirme (RBAC)
Sistem; Admin, Planlamaci, Operator ve Sevkiyatci rollerine göre dinamik arayüz kırılımları sunar:
- **Yönetici/Planlamacı**: Ürün ekleme/silme/güncelleme (CRUD), kritik stok seviyelerini izleme ve sipariş yönetimi.
- **Operatör**: Üretim giriş ekranında kendi personel bilgisi kilitli gelir (`Enabled = false`), böylece veri giriş hataları önlenir.
- **Sevkiyatçı**: Bekleyen sipariş listesini ve anlık stok yeterlilik durumlarını süzebilir.

### 📊 Akıllı Stok ve Sevkiyat Doğrulama
Sipariş sevk edilirken sistem backend tarafında stok kontrolü yapar. Sevk edilmek istenen miktar anlık stoktan fazla ise işlem durdurulur ve arayüze hata mesajı döner. Stok yeterliyse sevk işlemi tamamlanarak stok miktarı otomatik düşürülür.

### ⏱️ Makine Duruş ve Verimlilik Analizi
Masaüstü ve mobil ekranlardaki raporlar, fabrikadaki makinelerin durumlarını izleyerek duruş sebeplerini ve çalışma sürelerini raporlar.

---

## 📂 Proje Dizin Yapısı
```text
└── NexusERP/
    ├── backend/          # Spring Boot REST API Katmanı
    ├── client-desktop/   # C# Windows Forms Masaüstü Uygulaması
    ├── client-mobile/    # Kotlin & Jetpack Compose Android Uygulaması
    └── Builds/           # Derlenmiş hazır APK ve EXE paketleri
```

---

## 📥 Uygulamayı İndir (APK ve EXE)
Derlenmiş en güncel ve çalışmaya hazır dosyalara projenin [Builds](file:///c:/Users/Acer/Desktop/MiniERP_UretimTakip/Builds) dizininden erişebilirsiniz:
- [Android APK İndir](file:///c:/Users/Acer/Desktop/MiniERP_UretimTakip/Builds/NexusERP.apk)
- [Windows Masaüstü EXE İndir](file:///c:/Users/Acer/Desktop/MiniERP_UretimTakip/Builds/NexusERP_Desktop/NexusERP.exe)

---

## 💡 Öğrenilenler & Deneyimler
- **Çoklu Platform Veri Yönetimi**: Android mobil cihazlar ile C# masaüstü istemcilerin aynı merkez REST API üzerinde senkronize çalışmasını sağladım.
- **Barkod Okuyucu Entegrasyonu**: Google ML Kit kullanarak mobil kamerayı barkod tarayıcı olarak entegre etmeyi ve veri girişlerini otomatikleştirmeyi öğrendim.
- **Arayüz ve Tema Yönetimi**: Windows Forms uygulamasında dinamik açık/koyu tema kontrol mekanizmaları geliştirdim.
- **Veri Tohumlama**: Uygulamanın çalışması için gerekli olan varsayılan yapılandırma verilerini (varsayılan kullanıcılar ve roller) uygulama başlangıcında kontrol edip eklemeyi deneyimledim.

---

## Nasıl Çalıştırılır?

### 1. Backend API'yi Başlatma
1. `backend` klasörünü bir Java IDE'sinde açın.
2. Uygulamayı derleyin ve çalıştırın:
   ```bash
   mvn clean install
   mvn spring-boot:run
   ```

### 2. Masaüstü Uygulamasını Çalıştırma
1. Visual Studio veya Rider IDE'sini açın.
2. `client-desktop/NexusERP.slnx` çözüm dosyasını yükleyin.
3. Projeyi derleyip çalıştırın.

### 3. Mobil Uygulamayı Çalıştırma
1. Android Studio'yu açın.
2. `client-mobile/NexusERP` klasörünü içeri aktarın.
3. Projeyi derleyip çalıştırın.
