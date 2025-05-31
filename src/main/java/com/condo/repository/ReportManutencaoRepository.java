package com.condo.repository;

import com.condo.domain.ReportManutencao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReportManutencaoRepository extends JpaRepository<ReportManutencao, Long> {

    List<ReportManutencao> findByStatus(String status);

}