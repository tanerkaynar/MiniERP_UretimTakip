package com.tanerkaynar.nexuserp.repository;

import com.tanerkaynar.nexuserp.model.Kullanici;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface KullaniciRepository extends JpaRepository<Kullanici, Integer> {
    Optional<Kullanici> findByKullaniciadi(String kullaniciadi);
    boolean existsByKullaniciadi(String kullaniciadi);
    boolean existsByPersonelPersonelid(Integer personelid);
}