package com.tanerkaynar.nexuserp.repository;

import com.tanerkaynar.nexuserp.model.Makine;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface MakineRepository extends JpaRepository<Makine, Integer> {
    List<Makine> findByDurumOrderByMakineadi(String durum);
    List<Makine> findAllByOrderByMakineidDesc();
}