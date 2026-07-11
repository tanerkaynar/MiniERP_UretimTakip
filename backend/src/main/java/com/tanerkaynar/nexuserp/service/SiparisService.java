package com.tanerkaynar.nexuserp.service;

import com.tanerkaynar.nexuserp.model.*;
import com.tanerkaynar.nexuserp.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Service
public class SiparisService {

    @Autowired
    private SiparisRepository siparisRepository;

    @Autowired
    private SiparisDetayiRepository siparisDetayiRepository;

    @Autowired
    private MusteriRepository musteriRepository;

    @Autowired
    private UrunRepository urunRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public List<Map<String, Object>> siparisleriGetir() {
        return jdbcTemplate.queryForList("""
            SELECT
                s.siparisid,
                s.musteriid,
                sd.urunid,
                m.musteriadi,
                u.urunadi,
                sd.miktar,
                sd.birimfiyat,
                s.durum,
                s.siparistarihi
            FROM siparisler s
            INNER JOIN musteriler m ON s.musteriid = m.musteriid
            INNER JOIN siparisdetaylari sd ON s.siparisid = sd.siparisid
            INNER JOIN urunler u ON sd.urunid = u.urunid
            ORDER BY s.siparisid DESC
            LIMIT 50""");
    }

    public List<Map<String, Object>> sevkiyatBekleyenSiparisleriGetir() {
        return jdbcTemplate.queryForList("""
            SELECT
                s.siparisid,
                s.musteriid,
                sd.urunid,
                m.musteriadi,
                u.urunadi,
                sd.miktar,
                ur.stokmiktari AS mevcutstok,
                s.durum,
                s.siparistarihi
            FROM siparisler s
            INNER JOIN musteriler m ON s.musteriid = m.musteriid
            INNER JOIN siparisdetaylari sd ON s.siparisid = sd.siparisid
            INNER JOIN urunler u ON sd.urunid = u.urunid
            INNER JOIN urunler ur ON sd.urunid = ur.urunid
            WHERE s.durum = 'Hazirlaniyor'
            ORDER BY s.siparisid DESC""");
    }

    @Transactional
    public SiparisDetayi siparisKaydet(Integer musteriid, Integer urunid, Integer miktar, BigDecimal birimfiyat) {
        Musteri musteri = musteriRepository.findById(musteriid).orElseThrow(() -> new IllegalArgumentException("Müşteri bulunamadı."));
        Urun urun = urunRepository.findById(urunid).orElseThrow(() -> new IllegalArgumentException("Ürün bulunamadı."));

        Siparis siparis = new Siparis();
        siparis.setMusteri(musteri);
        siparis.setDurum("Hazirlaniyor");
        siparis = siparisRepository.save(siparis);

        SiparisDetayi detay = new SiparisDetayi();
        detay.setSiparis(siparis);
        detay.setUrun(urun);
        detay.setMiktar(miktar);
        detay.setBirimfiyat(birimfiyat);

        return siparisDetayiRepository.save(detay);
    }

    @Transactional
    public void sevkEt(Integer siparisid) {
        Siparis siparis = siparisRepository.findById(siparisid).orElseThrow(() -> new IllegalArgumentException("Sipariş bulunamadı."));
        List<SiparisDetayi> detaylar = siparisDetayiRepository.findBySiparisSiparisid(siparisid);
        if (detaylar.isEmpty()) {
            throw new IllegalArgumentException("Sipariş detayı bulunamadı.");
        }

        SiparisDetayi detay = detaylar.get(0);
        Urun urun = detay.getUrun();

        if (urun.getStokmiktari() < detay.getMiktar()) {
            throw new IllegalArgumentException("Yetersiz stok. Mevcut stok: " + urun.getStokmiktari() + ", istenen miktar: " + detay.getMiktar());
        }

        urun.setStokmiktari(urun.getStokmiktari() - detay.getMiktar());
        urunRepository.save(urun);

        siparis.setDurum("Sevk Edildi");
        siparisRepository.save(siparis);
    }

    @Transactional
    public void siparisSil(Integer siparisid) {
        Siparis siparis = siparisRepository.findById(siparisid).orElse(null);
        if (siparis != null) {
            siparisRepository.delete(siparis);
        }
    }

    @Transactional
    public SiparisDetayi siparisGuncelle(Integer siparisid, Integer musteriid, Integer urunid, Integer miktar, BigDecimal birimfiyat) {
        Siparis siparis = siparisRepository.findById(siparisid).orElseThrow(() -> new IllegalArgumentException("Sipariş bulunamadı."));
        Musteri musteri = musteriRepository.findById(musteriid).orElseThrow(() -> new IllegalArgumentException("Müşteri bulunamadı."));
        Urun urun = urunRepository.findById(urunid).orElseThrow(() -> new IllegalArgumentException("Ürün bulunamadı."));

        siparis.setMusteri(musteri);
        siparisRepository.save(siparis);

        List<SiparisDetayi> detaylar = siparisDetayiRepository.findBySiparisSiparisid(siparisid);
        SiparisDetayi detay;
        if (detaylar.isEmpty()) {
            detay = new SiparisDetayi();
            detay.setSiparis(siparis);
        } else {
            detay = detaylar.get(0);
        }
        detay.setUrun(urun);
        detay.setMiktar(miktar);
        detay.setBirimfiyat(birimfiyat);

        return siparisDetayiRepository.save(detay);
    }
}