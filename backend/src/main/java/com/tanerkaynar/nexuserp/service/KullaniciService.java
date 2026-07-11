package com.tanerkaynar.nexuserp.service;

import com.tanerkaynar.nexuserp.model.Kullanici;
import com.tanerkaynar.nexuserp.model.Personel;
import com.tanerkaynar.nexuserp.repository.KullaniciRepository;
import com.tanerkaynar.nexuserp.repository.PersonelRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Optional;

@Service
public class KullaniciService {

    @Autowired
    private KullaniciRepository repository;

    @Autowired
    private PersonelRepository personelRepository;

    public Optional<Kullanici> dogrula(String kullaniciadi, String parola) {
        String hash = sha256Hash(parola);
        Optional<Kullanici> optUser = repository.findByKullaniciadi(kullaniciadi.trim());
        if (optUser.isPresent()) {
            Kullanici user = optUser.get();
            if (user.getParolahash().equalsIgnoreCase(hash)) {
                if (!Boolean.TRUE.equals(user.getAktifmi())) {
                    throw new IllegalArgumentException("Hesap pasif durumdadır.");
                }
                if (user.getPersonel() != null && !Boolean.TRUE.equals(user.getPersonel().getAktifmi())) {
                    throw new IllegalArgumentException("İlişkili personel kaydı pasif durumdadır.");
                }
                return Optional.of(user);
            }
        }
        return Optional.empty();
    }

    public Kullanici kullaniciKaydet(String kullaniciadi, String parola, String rol, Integer personelid) {
        if (rol.trim().equalsIgnoreCase("Admin")) {
            throw new IllegalArgumentException("Yönetici (Admin) rolünde yeni bir kullanıcı kaydedilemez.");
        }

        if (repository.existsByKullaniciadi(kullaniciadi.trim())) {
            throw new IllegalArgumentException("Bu kullanıcı adı zaten alınmış.");
        }

        Kullanici user = new Kullanici();
        user.setKullaniciadi(kullaniciadi.trim());
        user.setParolahash(sha256Hash(parola).toLowerCase());
        user.setRol(rol.trim());
        user.setAktifmi(true);

        if (personelid != null) {
            Personel personel = personelRepository.findById(personelid)
                .orElseThrow(() -> new IllegalArgumentException("Seçilen personel bulunamadı."));

            if (repository.existsByPersonelPersonelid(personelid)) {
                throw new IllegalArgumentException("Bu personel zaten başka bir kullanıcıya tanımlanmış.");
            }

            user.setPersonel(personel);
        } else if (rol.trim().equalsIgnoreCase("Operator")) {
            throw new IllegalArgumentException("Operatör rolü için personel seçimi zorunludur.");
        }

        return repository.save(user);
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