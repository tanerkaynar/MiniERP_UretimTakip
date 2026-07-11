package com.tanerkaynar.nexuserp.repository;

import com.tanerkaynar.nexuserp.model.IslemLogi;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface IslemLogiRepository extends JpaRepository<IslemLogi, Integer> {
    List<IslemLogi> findTop100ByOrderByLogidDesc();
}