package com.condo.repository;

import com.condo.domain.AreaComum;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface AreaComumRepository extends JpaRepository<AreaComum, Long> {
    Optional<AreaComum> findByNome(String nome);
}