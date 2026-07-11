package com.tanerkaynar.nexuserp.controller;

import com.tanerkaynar.nexuserp.model.SiparisDetayi;
import com.tanerkaynar.nexuserp.service.SiparisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/siparisler")
public class SiparisController {

    @Autowired
    private SiparisService service;

    @GetMapping
    public List<Map<String, Object>> getAll() {
        return service.siparisleriGetir();
    }

    @GetMapping("/bekleyen")
    public List<Map<String, Object>> getPending() {
        return service.sevkiyatBekleyenSiparisleriGetir();
    }

    @PostMapping
    public ResponseEntity<?> create(@RequestBody Map<String, Object> request) {
        try {
            Integer musteriid = ConvertToInteger(request.get("musteriid"));
            Integer urunid = ConvertToInteger(request.get("urunid"));
            Integer miktar = ConvertToInteger(request.get("miktar"));
            BigDecimal birimfiyat = ConvertToBigDecimal(request.get("birimfiyat"));

            if (musteriid == null || urunid == null || miktar == null || birimfiyat == null) {
                return ResponseEntity.badRequest().body("Tüm alanlar zorunludur.");
            }

            SiparisDetayi saved = service.siparisKaydet(musteriid, urunid, miktar, birimfiyat);
            return ResponseEntity.ok(saved);
        } catch (Exception ex) {
            return ResponseEntity.badRequest().body(ex.getMessage());
        }
    }

    @PostMapping("/{id}/sevk")
    public ResponseEntity<?> ship(@PathVariable Integer id) {
        try {
            service.sevkEt(id);
            return ResponseEntity.ok().build();
        } catch (Exception ex) {
            return ResponseEntity.badRequest().body(ex.getMessage());
        }
    }

    private Integer ConvertToInteger(Object value) {
        if (value == null) return null;
        if (value instanceof Number) {
            return ((Number) value).intValue();
        }
        try {
            return Integer.parseInt(value.toString());
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private BigDecimal ConvertToBigDecimal(Object value) {
        if (value == null) return null;
        if (value instanceof BigDecimal) return (BigDecimal) value;
        try {
            return new BigDecimal(value.toString());
        } catch (Exception e) {
            return null;
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Integer id, @RequestBody Map<String, Object> request) {
        try {
            Integer musteriid = ConvertToInteger(request.get("musteriid"));
            Integer urunid = ConvertToInteger(request.get("urunid"));
            Integer miktar = ConvertToInteger(request.get("miktar"));
            BigDecimal birimfiyat = ConvertToBigDecimal(request.get("birimfiyat"));

            if (musteriid == null || urunid == null || miktar == null || birimfiyat == null) {
                return ResponseEntity.badRequest().body("Tüm alanlar zorunludur.");
            }

            SiparisDetayi updated = service.siparisGuncelle(id, musteriid, urunid, miktar, birimfiyat);
            return ResponseEntity.ok(updated);
        } catch (Exception ex) {
            return ResponseEntity.badRequest().body(ex.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Integer id) {
        try {
            service.siparisSil(id);
            return ResponseEntity.ok().build();
        } catch (Exception ex) {
            return ResponseEntity.badRequest().body(ex.getMessage());
        }
    }
}