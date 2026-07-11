package com.tanerkaynar.nexuserp.controller;

import com.tanerkaynar.nexuserp.model.Kullanici;
import com.tanerkaynar.nexuserp.service.KullaniciService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/kullanicilar")
public class KullaniciController {

    @Autowired
    private KullaniciService service;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> request) {
        String username = request.get("kullaniciadi");
        String password = request.get("parola");

        if (username == null || password == null) {
            return ResponseEntity.badRequest().body("Kullanıcı adı ve parola zorunludur.");
        }

        Optional<Kullanici> user = service.dogrula(username, password);
        if (user.isPresent()) {
            return ResponseEntity.ok(user.get());
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Kullanıcı adı veya şifre hatalı ya da hesap pasif.");
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody Map<String, Object> request) {
        String username = (String) request.get("kullaniciadi");
        String password = (String) request.get("parola");
        String role = (String) request.get("rol");
        Integer personelid = null;
        if (request.containsKey("personelid") && request.get("personelid") != null) {
            try {
                personelid = Integer.parseInt(request.get("personelid").toString());
            } catch (NumberFormatException ignored) {}
        }

        if (username == null || password == null || role == null) {
            return ResponseEntity.badRequest().body("Tüm alanlar zorunludur.");
        }

        try {
            Kullanici saved = service.kullaniciKaydet(username, password, role, personelid);
            return ResponseEntity.ok(saved);
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(ex.getMessage());
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Kayıt sırasında hata oluştu: " + ex.getMessage());
        }
    }
}