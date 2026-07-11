package com.tanerkaynar.nexuserp.controller;

import com.tanerkaynar.nexuserp.model.Urun;
import com.tanerkaynar.nexuserp.repository.UrunRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/urunler")
public class UrunController {

    @Autowired
    private UrunRepository repository;

    @Autowired
    private org.springframework.jdbc.core.JdbcTemplate jdbcTemplate;

    @GetMapping("/stok-takip")
    public List<Map<String, Object>> getStokTakip() {
        return jdbcTemplate.queryForList("""
            SELECT
                urunid, urunadi, stokmiktari, birimfiyat, stokmiktari * birimfiyat AS stokdegeri,
                CASE
                    WHEN stokmiktari <= 10 THEN 'Kritik'
                    WHEN stokmiktari <= 50 THEN 'Azaliyor'
                    ELSE 'Yeterli'
                END AS stokdurumu
            FROM urunler
            WHERE aktifmi = TRUE
            ORDER BY stokmiktari ASC""");
    }

    @GetMapping
    public List<Urun> getAll() {
        return repository.findAllByOrderByUrunidDesc();
    }

    @GetMapping("/aktif")
    public List<Urun> getActive() {
        return repository.findByAktifmiTrueOrderByUrunadi();
    }

    @GetMapping("/{id}/stok")
    public ResponseEntity<Integer> getStok(@PathVariable Integer id) {
        Optional<Urun> opt = repository.findById(id);
        return opt.map(urun -> ResponseEntity.ok(urun.getStokmiktari()))
                  .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public Urun create(@RequestBody Urun urun) {
        urun.setAktifmi(true);
        return repository.save(urun);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Urun> update(@PathVariable Integer id, @RequestBody Urun request) {
        return repository.findById(id).map(urun -> {
            urun.setUrunadi(request.getUrunadi());
            urun.setStokmiktari(request.getStokmiktari());
            urun.setBirimfiyat(request.getBirimfiyat());
            return ResponseEntity.ok(repository.save(urun));
        }).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Integer id) {
        return repository.findById(id).map(urun -> {
            repository.delete(urun);
            return ResponseEntity.ok().build();
        }).orElseGet(() -> ResponseEntity.notFound().build());
    }
}