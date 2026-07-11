package com.tanerkaynar.nexuserp.repository;

import com.tanerkaynar.nexuserp.model.UretimKaydi;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDateTime;
import java.util.List;

public interface UretimKaydiRepository extends JpaRepository<UretimKaydi, Integer> {
    List<UretimKaydi> findTop50ByOrderByUretimtarihiDesc();
    List<UretimKaydi> findByUretimtarihiBetweenOrderByUretimtarihiDesc(LocalDateTime start, LocalDateTime end);
}