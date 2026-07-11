package com.tanerkaynar.nexuserp.repository;

import com.tanerkaynar.nexuserp.model.Urun;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface UrunRepository extends JpaRepository<Urun, Integer> {
    List<Urun> findByAktifmiTrueOrderByUrunadi();
    List<Urun> findAllByOrderByUrunidDesc();
}