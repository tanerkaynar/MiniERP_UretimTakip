package com.tanerkaynar.nexuserp.controller;

import com.tanerkaynar.nexuserp.model.Musteri;
import com.tanerkaynar.nexuserp.model.Makine;
import com.tanerkaynar.nexuserp.model.Personel;
import com.tanerkaynar.nexuserp.repository.MusteriRepository;
import com.tanerkaynar.nexuserp.repository.MakineRepository;
import com.tanerkaynar.nexuserp.repository.PersonelRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class TanimlamaController {

    @Autowired
    private MusteriRepository musteriRepository;

    @Autowired
    private MakineRepository makineRepository;

    @Autowired
    private PersonelRepository personelRepository;

    @GetMapping("/musteriler")
    public List<Musteri> getAllMusteriler() {
        return musteriRepository.findAllByOrderByMusteriidDesc();
    }

    @PostMapping("/musteriler")
    public Musteri createMusteri(@RequestBody Musteri musteri) {
        return musteriRepository.save(musteri);
    }

    @PutMapping("/musteriler/{id}")
    public ResponseEntity<Musteri> updateMusteri(@PathVariable Integer id, @RequestBody Musteri request) {
        return musteriRepository.findById(id).map(m -> {
            m.setMusteriadi(request.getMusteriadi());
            return ResponseEntity.ok(musteriRepository.save(m));
        }).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/musteriler/{id}")
    public ResponseEntity<?> deleteMusteri(@PathVariable Integer id) {
        return musteriRepository.findById(id).map(m -> {
            musteriRepository.delete(m);
            return ResponseEntity.ok().build();
        }).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/makineler")
    public List<Makine> getAllMakineler() {
        return makineRepository.findAllByOrderByMakineidDesc();
    }

    @GetMapping("/makineler/aktif")
    public List<Makine> getActiveMakineler() {
        return makineRepository.findByDurumOrderByMakineadi("Aktif");
    }

    @PostMapping("/makineler")
    public Makine createMakine(@RequestBody Makine makine) {
        return makineRepository.save(makine);
    }

    @PutMapping("/makineler/{id}")
    public ResponseEntity<Makine> updateMakine(@PathVariable Integer id, @RequestBody Makine request) {
        return makineRepository.findById(id).map(m -> {
            m.setMakineadi(request.getMakineadi());
            m.setMakinekodu(request.getMakinekodu());
            m.setDurum(request.getDurum());
            return ResponseEntity.ok(makineRepository.save(m));
        }).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/makineler/{id}")
    public ResponseEntity<?> deleteMakine(@PathVariable Integer id) {
        return makineRepository.findById(id).map(m -> {
            makineRepository.delete(m);
            return ResponseEntity.ok().build();
        }).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/personeller")
    public List<Personel> getAllPersoneller() {
        return personelRepository.findAllByOrderByPersonelidDesc();
    }

    @GetMapping("/personeller/aktif")
    public List<Personel> getActivePersoneller() {
        return personelRepository.findByAktifmiTrueOrderByAdsoyad();
    }

    @GetMapping("/personeller/kullanici-atanmamis")
    public List<Personel> getUnmappedActivePersoneller() {
        return personelRepository.findAktifVeKullanicisizPersoneller();
    }

    @PostMapping("/personeller")
    public Personel createPersonel(@RequestBody Personel personel) {
        return personelRepository.save(personel);
    }

    @PutMapping("/personeller/{id}")
    public ResponseEntity<Personel> updatePersonel(@PathVariable Integer id, @RequestBody Personel request) {
        return personelRepository.findById(id).map(p -> {
            p.setAdsoyad(request.getAdsoyad());
            p.setDepartman(request.getDepartman());
            p.setAktifmi(request.getAktifmi());
            return ResponseEntity.ok(personelRepository.save(p));
        }).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/personeller/{id}")
    public ResponseEntity<?> deletePersonel(@PathVariable Integer id) {
        return personelRepository.findById(id).map(p -> {
            personelRepository.delete(p);
            return ResponseEntity.ok().build();
        }).orElseGet(() -> ResponseEntity.notFound().build());
    }
}