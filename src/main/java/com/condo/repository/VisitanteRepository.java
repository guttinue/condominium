package com.condo.repository;

import com.condo.domain.Condominium;
import com.condo.domain.Morador;
import com.condo.domain.Visitante;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface VisitanteRepository extends JpaRepository<Visitante, Long>, JpaSpecificationExecutor<Visitante> {

    List<Visitante> findByCondominioAndStatusVisitaAndDataHoraVisitaPrevistaBetween(
            Condominium condominio, String statusVisita, LocalDateTime inicioDoDia, LocalDateTime fimDoDia);

    List<Visitante> findByMoradorResponsavelAndStatusVisitaOrderByDataHoraVisitaPrevistaAsc(
            Morador moradorResponsavel, String statusVisita);

    List<Visitante> findByMoradorResponsavelOrderByDataHoraVisitaPrevistaDesc(Morador moradorResponsavel);


    List<Visitante> findByCondominioAndNomeVisitanteContainingIgnoreCaseOrderByDataHoraVisitaPrevistaDesc(
            Condominium condominio, String nomeVisitante);

    List<Visitante> findByCondominioAndUnidadeDestinoIgnoreCaseOrderByDataHoraVisitaPrevistaDesc(
            Condominium condominio, String unidadeDestino);

    List<Visitante> findByCondominioAndDataHoraVisitaPrevistaBetweenOrderByDataHoraVisitaPrevistaDesc(
            Condominium condominio, LocalDateTime inicio, LocalDateTime fim);

    List<Visitante> findByCondominioOrderByDataHoraVisitaPrevistaDesc(Condominium condominio);


}