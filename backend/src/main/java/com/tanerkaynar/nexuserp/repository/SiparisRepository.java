package com.tanerkaynar.nexuserp.repository;

import com.tanerkaynar.nexuserp.model.Siparis;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface SiparisRepository extends JpaRepository<Siparis, Integer> {
    List<Siparis> findTop50ByOrderBySiparisidDesc();
    List<Siparis> findByDurumOrderBySiparisidDesc(String durum);
}