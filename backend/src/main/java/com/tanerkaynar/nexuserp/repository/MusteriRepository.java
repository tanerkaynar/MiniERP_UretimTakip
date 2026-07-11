package com.tanerkaynar.nexuserp.repository;

import com.tanerkaynar.nexuserp.model.Musteri;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface MusteriRepository extends JpaRepository<Musteri, Integer> {
    List<Musteri> findAllByOrderByMusteriadi();
    List<Musteri> findAllByOrderByMusteriidDesc();
}