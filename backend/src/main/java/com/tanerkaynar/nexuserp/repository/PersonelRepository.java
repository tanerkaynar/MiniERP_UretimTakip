package com.tanerkaynar.nexuserp.repository;

import com.tanerkaynar.nexuserp.model.Personel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;

public interface PersonelRepository extends JpaRepository<Personel, Integer> {
    List<Personel> findByAktifmiTrueOrderByAdsoyad();
    List<Personel> findAllByOrderByPersonelidDesc();

    @Query("SELECT p FROM Personel p WHERE p.aktifmi = true AND p.personelid NOT IN (SELECT k.personel.personelid FROM Kullanici k WHERE k.personel IS NOT NULL) ORDER BY p.adsoyad")
    List<Personel> findAktifVeKullanicisizPersoneller();
}