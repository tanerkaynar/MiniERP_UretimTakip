package com.tanerkaynar.nexuserp.controller;

import com.tanerkaynar.nexuserp.model.UretimKaydi;
import com.tanerkaynar.nexuserp.service.UretimService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/uretim")
public class UretimController {

    @Autowired
    private UretimService service;

    @GetMapping
    public List<Map<String, Object>> getAll() {
        return service.tumUretimKayitlariniGetir();
    }

    @PostMapping
    public ResponseEntity<?> create(@RequestBody Map<String, Object> request) {
        try {
            Integer urunid = ConvertToInteger(request.get("urunid"));
            Integer makineid = ConvertToInteger(request.get("makineid"));
            Integer personelid = ConvertToInteger(request.get("personelid"));
            Integer uretimadedi = ConvertToInteger(request.get("uretimadedi"));
            String aciklama = (String) request.get("aciklama");

            if (urunid == null || makineid == null || personelid == null || uretimadedi == null) {
                return ResponseEntity.badRequest().body("Tüm alanlar zorunludur.");
            }

            UretimKaydi saved = service.uretimKaydet(urunid, makineid, personelid, uretimadedi, aciklama);
            return ResponseEntity.ok(saved);
        } catch (Exception ex) {
            return ResponseEntity.badRequest().body(ex.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Integer id) {
        try {
            service.uretimSil(id);
            return ResponseEntity.ok().build();
        } catch (Exception ex) {
            return ResponseEntity.badRequest().body(ex.getMessage());
        }
    }

    @GetMapping("/rapor")
    public List<Map<String, Object>> getRapor(
            @RequestParam("baslangic") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate baslangic,
            @RequestParam("bitis") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate bitis) {
        return service.tarihliUretimRaporuGetir(baslangic, bitis);
    }

    @GetMapping("/gruplu")
    public List<Map<String, Object>> getGruplu(@RequestParam("tip") String tip) {
        return service.grupluRaporGetir(tip);
    }

    @GetMapping("/durus")
    public List<Map<String, Object>> getDurus(
            @RequestParam("baslangic") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate baslangic,
            @RequestParam("bitis") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate bitis) {
        return service.durusAnaliziGetir(baslangic, bitis);
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
}