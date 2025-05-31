package com.condo.repository;

import com.condo.domain.Condominium;
import com.condo.domain.Morador;
import com.condo.domain.Veiculo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface VeiculoRepository extends JpaRepository<Veiculo, Long> {

    boolean existsByPlacaAndCondominio(String placa, Condominium condominio);

    List<Veiculo> findByMoradorOrderByModeloAsc(Morador morador);

    List<Veiculo> findByCondominioOrderByMorador_UnidadeAscPlacaAsc(Condominium condominio);

    Optional<Veiculo> findByIdAndMorador(Long id, Morador morador);
}