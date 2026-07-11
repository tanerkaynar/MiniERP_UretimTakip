package com.tanerkaynar.nexuserp.service;

import com.tanerkaynar.nexuserp.model.Kullanici;
import com.tanerkaynar.nexuserp.model.Personel;
import com.tanerkaynar.nexuserp.repository.KullaniciRepository;
import com.tanerkaynar.nexuserp.repository.PersonelRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

@Component
public class DataInitializer implements CommandLineRunner {

    private final KullaniciRepository kullaniciRepository;
    private final PersonelRepository personelRepository;

    public DataInitializer(KullaniciRepository kullaniciRepository, PersonelRepository personelRepository) {
        this.kullaniciRepository = kullaniciRepository;
        this.personelRepository = personelRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        if (!kullaniciRepository.existsByKullaniciadi("admin")) {
            System.out.println("Admin user not found. Re-creating default admin user...");

            Personel adminPersonel = personelRepository.findAll().stream()
                .filter(p -> p.getAdsoyad().equalsIgnoreCase("Talat Kaynar"))
                .findFirst()
                .orElseGet(() -> {
                    Personel p = new Personel();
                    p.setAdsoyad("Talat Kaynar");
                    p.setDepartman("Yönetim");
                    p.setAktifmi(true);
                    return personelRepository.save(p);
                });

            Kullanici adminUser = new Kullanici();
            adminUser.setKullaniciadi("admin");
            adminUser.setRol("Admin");
            adminUser.setAktifmi(true);
            adminUser.setParolahash(sha256Hash("123456"));
            adminUser.setPersonel(adminPersonel);
            
            kullaniciRepository.save(adminUser);
            System.out.println("Default admin user created successfully! (Username: admin, Password: 123456)");
        }
    }

    private String sha256Hash(String text) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = digest.digest(text.getBytes(StandardCharsets.UTF_8));
            StringBuilder hexString = new StringBuilder();
            for (byte b : hashBytes) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }
}