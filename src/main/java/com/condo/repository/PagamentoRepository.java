package com.condo.repository;

import com.condo.domain.Morador;
import com.condo.domain.Pagamento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PagamentoRepository extends JpaRepository<Pagamento, Long> {

    List<Pagamento> findByMoradorAndStatusIgnoreCaseOrderByDataVencimentoAsc(Morador morador, String status);
    List<Pagamento> findByStatusIgnoreCaseOrderByDataVencimentoAsc(String status);
}