package com.condo.repository;

import com.condo.domain.Morador;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface MoradorRepository extends JpaRepository<Morador, Long> {
    Optional<Morador> findByCpf(String cpf);

    List<Morador> findByUnidade(String unidade); // Mantém o método original se precisar dele

    List<Morador> findByUnidadeIgnoreCase(String unidade); // <-- Novo método adicionado
}