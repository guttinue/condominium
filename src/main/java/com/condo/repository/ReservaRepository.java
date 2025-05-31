package com.condo.repository;

import com.condo.domain.Morador;
import com.condo.domain.Reserva;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReservaRepository extends JpaRepository<Reserva, Long> {

    List<Reserva> findByStatus(String status);

    List<Reserva> findByMorador(Morador morador);

}