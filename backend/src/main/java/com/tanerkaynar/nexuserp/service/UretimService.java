package com.tanerkaynar.nexuserp.service;

import com.tanerkaynar.nexuserp.model.*;
import com.tanerkaynar.nexuserp.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;

@Service
public class UretimService {

    @Autowired
    private UretimKaydiRepository repository;

    @Autowired
    private UrunRepository urunRepository;

    @Autowired
    private MakineRepository makineRepository;

    @Autowired
    private PersonelRepository personelRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public List<Map<String, Object>> tumUretimKayitlariniGetir() {
        return jdbcTemplate.queryForList("""
            SELECT
                uk.uretimid,
                u.urunadi,
                m.makineadi,
                p.adsoyad AS personeladi,
                uk.uretimadedi,
                uk.uretimtarihi,
                uk.aciklama
            FROM uretimkayitlari uk
            INNER JOIN urunler u ON uk.urunid = u.urunid
            INNER JOIN makineler m ON uk.makineid = m.makineid
            INNER JOIN personeller p ON uk.personelid = p.personelid
            ORDER BY uk.uretimtarihi DESC
            LIMIT 50""");
    }

    public List<Map<String, Object>> tarihliUretimRaporuGetir(LocalDate baslangic, LocalDate bitis) {
        LocalDateTime start = baslangic.atStartOfDay();
        LocalDateTime end = bitis.atTime(LocalTime.MAX);
        return jdbcTemplate.queryForList("""
            SELECT
                uk.uretimid,
                u.urunadi,
                m.makineadi,
                p.adsoyad AS personeladi,
                uk.uretimadedi,
                uk.uretimtarihi
            FROM uretimkayitlari uk
            INNER JOIN urunler u ON uk.urunid = u.urunid
            INNER JOIN makineler m ON uk.makineid = m.makineid
            INNER JOIN personeller p ON uk.personelid = p.personelid
            WHERE uk.uretimtarihi >= ? AND uk.uretimtarihi <= ?
            ORDER BY uk.uretimtarihi DESC""", start, end);
    }

    public List<Map<String, Object>> grupluRaporGetir(String tip) {
        String query;
        if ("Urun Bazli".equalsIgnoreCase(tip)) {
            query = """
                SELECT
                    u.urunadi,
                    COUNT(uk.uretimid) AS uretimkaydisayisi,
                    SUM(uk.uretimadedi) AS toplamuretimadedi
                FROM uretimkayitlari uk
                INNER JOIN urunler u ON uk.urunid = u.urunid
                GROUP BY u.urunadi
                ORDER BY toplamuretimadedi DESC""";
        } else if ("Personel Bazli".equalsIgnoreCase(tip)) {
            query = """
                SELECT
                    p.adsoyad AS personeladi,
                    COUNT(uk.uretimid) AS uretimkaydisayisi,
                    SUM(uk.uretimadedi) AS toplamuretimadedi
                FROM uretimkayitlari uk
                INNER JOIN personeller p ON uk.personelid = p.personelid
                GROUP BY p.adsoyad
                ORDER BY toplamuretimadedi DESC""";
        } else {
            query = """
                SELECT
                    m.makineadi,
                    COUNT(uk.uretimid) AS uretimkaydisayisi,
                    SUM(uk.uretimadedi) AS toplamuretimadedi,
                    AVG(CAST(uk.uretimadedi AS DECIMAL(18,2))) AS ortalamauretim
                FROM uretimkayitlari uk
                INNER JOIN makineler m ON uk.makineid = m.makineid
                GROUP BY m.makineadi
                ORDER BY toplamuretimadedi DESC""";
        }
        return jdbcTemplate.queryForList(query);
    }

    public List<Map<String, Object>> durusAnaliziGetir(LocalDate baslangic, LocalDate bitis) {
        LocalDateTime start = baslangic.atStartOfDay();
        LocalDateTime end = bitis.atTime(LocalTime.MAX);
        return jdbcTemplate.queryForList("""
            SELECT
                m.makineid, m.makineadi, m.makinekodu, m.durum
            FROM makineler m
            LEFT JOIN uretimkayitlari uk
                ON m.makineid = uk.makineid
               AND uk.uretimtarihi >= ?
               AND uk.uretimtarihi <= ?
            WHERE uk.uretimid IS NULL
            ORDER BY m.makineadi""", start, end);
    }

    @Transactional
    public UretimKaydi uretimKaydet(Integer urunid, Integer makineid, Integer personelid, Integer uretimadedi, String aciklama) {
        Urun urun = urunRepository.findById(urunid).orElseThrow(() -> new IllegalArgumentException("Ürün bulunamadı."));
        Makine makine = makineRepository.findById(makineid).orElseThrow(() -> new IllegalArgumentException("Makine bulunamadı."));
        Personel personel = personelRepository.findById(personelid).orElseThrow(() -> new IllegalArgumentException("Personel bulunamadı."));

        UretimKaydi kayit = new UretimKaydi();
        kayit.setUrun(urun);
        kayit.setMakine(makine);
        kayit.setPersonel(personel);
        kayit.setUretimadedi(uretimadedi);
        kayit.setAciklama(aciklama);

        urun.setStokmiktari(urun.getStokmiktari() + uretimadedi);
        urunRepository.save(urun);

        return repository.save(kayit);
    }

    @Transactional
    public void uretimSil(Integer uretimid) {
        UretimKaydi kayit = repository.findById(uretimid).orElseThrow(() -> new IllegalArgumentException("Üretim kaydı bulunamadı."));
        Urun urun = kayit.getUrun();

        if (urun.getStokmiktari() < kayit.getUretimadedi()) {
            throw new IllegalArgumentException("Stok miktarı silinmek istenen üretim miktarından az olduğu için silme işlemi gerçekleştirilemez.");
        }

        urun.setStokmiktari(urun.getStokmiktari() - kayit.getUretimadedi());
        urunRepository.save(urun);

        repository.delete(kayit);
    }
}