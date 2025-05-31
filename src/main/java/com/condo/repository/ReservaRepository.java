// src/main/java/com/condo/repository/ReservaRepository.java
package com.condo.repository;

import com.condo.domain.AreaComum;
import com.condo.domain.Condominium;
import com.condo.domain.Morador;
import com.condo.domain.Reserva;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface ReservaRepository extends JpaRepository<Reserva, Long> {

    List<Reserva> findByMorador(Morador morador);
    List<Reserva> findByAreaComum(AreaComum areaComum);
    List<Reserva> findByAreaComumAndData(AreaComum areaComum, LocalDate data);
    List<Reserva> findByStatus(String status); // Recomendo usar findByStatusIgnoreCase se o case não for garantido

    // Query para buscar todas as reservas de um condomínio
    @Query("SELECT r FROM Reserva r WHERE r.areaComum.condominio = :condominio ORDER BY r.data DESC, r.horaInicio DESC")
    List<Reserva> findAllByCondominio(@Param("condominio") Condominium condominio);

    // Query para buscar reservas de um condomínio por status
    // O atributo aqui é 'condominio' (minúsculo), conforme definido em AreaComum.java
    @Query("SELECT r FROM Reserva r WHERE r.areaComum.condominio = :condominio AND UPPER(r.status) = UPPER(:status) ORDER BY r.data ASC, r.horaInicio ASC")
    List<Reserva> findAllByCondominioAndStatus(@Param("condominio") Condominium condominio, @Param("status") String status);
}