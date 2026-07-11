package com.tanerkaynar.nexuserp.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @GetMapping("/stats")
    public Map<String, Object> getStats(@RequestParam("rol") String rol) {
        Map<String, Object> stats = new HashMap<>();

        if ("Admin".equalsIgnoreCase(rol) || "Planlamaci".equalsIgnoreCase(rol)) {
            int urunSayisi = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM urunler WHERE aktifmi = TRUE", Integer.class);
            int toplamUretim = jdbcTemplate.queryForObject("SELECT COALESCE(SUM(uretimadedi), 0) FROM uretimkayitlari", Integer.class);
            int aktifMakine = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM makineler WHERE durum = 'Aktif'", Integer.class);
            int kritikStok = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM urunler WHERE aktifmi = TRUE AND stokmiktari <= 10", Integer.class);

            stats.put("urunSayisi", urunSayisi);
            stats.put("toplamUretim", toplamUretim);
            stats.put("aktifMakine", aktifMakine);
            stats.put("kritikStok", kritikStok);
        } else if ("Operator".equalsIgnoreCase(rol)) {
            int bugunkuUretim = jdbcTemplate.queryForObject("SELECT COALESCE(SUM(uretimadedi), 0) FROM uretimkayitlari WHERE uretimtarihi >= CURRENT_DATE", Integer.class);
            int calisanMakine = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM makineler WHERE durum = 'Aktif'", Integer.class);
            
            String sonUretimStr = "-";
            try {
                java.sql.Timestamp ts = jdbcTemplate.queryForObject("SELECT uretimtarihi FROM uretimkayitlari ORDER BY uretimid DESC LIMIT 1", java.sql.Timestamp.class);
                if (ts != null) {
                    sonUretimStr = new java.text.SimpleDateFormat("HH:mm").format(ts);
                }
            } catch (Exception e) {
                
            }

            stats.put("bugunkuUretim", bugunkuUretim);
            stats.put("calisanMakine", calisanMakine);
            stats.put("sonUretimStr", sonUretimStr);
        } else if ("Sevkiyatci".equalsIgnoreCase(rol)) {
            int bekleyenSiparis = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM siparisler WHERE durum = 'Hazirlaniyor'", Integer.class);
            int kritikStok = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM urunler WHERE aktifmi = TRUE AND stokmiktari <= 10", Integer.class);
            int sevkEdilen = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM siparisler WHERE durum = 'Sevk Edildi'", Integer.class);

            stats.put("bekleyenSiparis", bekleyenSiparis);
            stats.put("kritikStok", kritikStok);
            stats.put("sevkEdilen", sevkEdilen);
        }

        return stats;
    }
}