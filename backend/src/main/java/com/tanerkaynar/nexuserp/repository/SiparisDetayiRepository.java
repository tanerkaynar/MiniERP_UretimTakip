package com.tanerkaynar.nexuserp.repository;

import com.tanerkaynar.nexuserp.model.Siparis;
import com.tanerkaynar.nexuserp.model.SiparisDetayi;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface SiparisDetayiRepository extends JpaRepository<SiparisDetayi, Integer> {
    List<SiparisDetayi> findBySiparisSiparisid(Integer siparisid);
    List<SiparisDetayi> findBySiparisIn(List<Siparis> siparisler);
}