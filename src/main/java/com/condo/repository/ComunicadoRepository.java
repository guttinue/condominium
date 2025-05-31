package com.condo.repository;

import com.condo.domain.Comunicado;
import com.condo.domain.Condominium;
import com.condo.domain.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ComunicadoRepository extends JpaRepository<Comunicado, Long> {

    List<Comunicado> findByCondominioOrderByDataPublicacaoDesc(Condominium condominio);

    List<Comunicado> findBySindicoOrderByDataPublicacaoDesc(Usuario sindico);
}