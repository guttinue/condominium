package com.condo.service;

import com.condo.domain.Condominium;
import com.condo.domain.Morador;
import com.condo.domain.Visitante;
import com.condo.repository.CondominiumRepository;
import com.condo.repository.MoradorRepository;
import com.condo.repository.VisitanteRepository;
// Importe Specification e classes relacionadas se for usar JpaSpecificationExecutor
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList; // Para Specification
import java.util.List;
import java.util.Optional; // Para os filtros do histórico

@Service
public class VisitanteService {

    private static final Logger log = LoggerFactory.getLogger(VisitanteService.class);

    private final VisitanteRepository visitanteRepository;
    private final MoradorRepository moradorRepository; // Para buscar Morador ao registrar
    private final CondominiumRepository condominiumRepository; // Para escopo de condomínio

    @Autowired
    public VisitanteService(VisitanteRepository visitanteRepository,
                            MoradorRepository moradorRepository,
                            CondominiumRepository condominiumRepository) {
        this.visitanteRepository = visitanteRepository;
        this.moradorRepository = moradorRepository;
        this.condominiumRepository = condominiumRepository;
    }

    @Transactional
    public Visitante registrarVisitante(Long moradorResponsavelId, String nomeVisitante,
                                        LocalDateTime dataHoraVisitaPrevista, String unidadeDestino,
                                        String documentoVisitante, String placaVeiculo, Long condominioId) {
        Morador morador = moradorRepository.findById(moradorResponsavelId)
                .orElseThrow(() -> new IllegalArgumentException("Morador responsável com ID " + moradorResponsavelId + " não encontrado."));

        Condominium condominio = condominiumRepository.findById(condominioId)
                .orElseThrow(() -> new IllegalArgumentException("Condomínio com ID " + condominioId + " não encontrado."));

        if (nomeVisitante == null || nomeVisitante.trim().isEmpty()) {
            throw new IllegalArgumentException("Nome do visitante é obrigatório.");
        }
        if (dataHoraVisitaPrevista == null || dataHoraVisitaPrevista.isBefore(LocalDateTime.now().minusMinutes(5))) { // Pequena tolerância
            throw new IllegalArgumentException("Data/hora da visita prevista inválida ou no passado.");
        }
        if (unidadeDestino == null || unidadeDestino.trim().isEmpty()) {
            throw new IllegalArgumentException("Unidade de destino é obrigatória.");
        }

        Visitante novoVisitante = new Visitante(nomeVisitante.trim(), dataHoraVisitaPrevista, unidadeDestino.trim(),
                morador, condominio,
                documentoVisitante != null ? documentoVisitante.trim() : null,
                placaVeiculo != null ? placaVeiculo.trim() : null);
        log.info("Registrando novo visitante: {} para unidade {} por morador {}",
                novoVisitante.getNomeVisitante(), novoVisitante.getUnidadeDestino(), morador.getNome());
        return visitanteRepository.save(novoVisitante);
    }

    /**
     * Lista os visitantes com status "ESPERADO" para o dia atual e para um condomínio específico.
     */
    public List<Visitante> listarVisitantesEsperadosHoje(Long condominioId) {
        Condominium condominio = condominiumRepository.findById(condominioId)
                .orElseThrow(() -> new IllegalArgumentException("Condomínio com ID " + condominioId + " não encontrado."));

        LocalDateTime inicioDoDia = LocalDate.now().atStartOfDay();
        LocalDateTime fimDoDia = LocalDate.now().atTime(LocalTime.MAX);

        log.info("Buscando visitantes esperados para hoje ({}) no condomínio {}", LocalDate.now(), condominio.getNome());
        return visitanteRepository.findByCondominioAndStatusVisitaAndDataHoraVisitaPrevistaBetween(
                condominio, "ESPERADO", inicioDoDia, fimDoDia);
    }

    /**
     * Lista os visitantes com status "ESPERADO" para um morador específico.
     */
    public List<Visitante> listarVisitantesEsperadosPorMorador(Long moradorId) {
        Morador morador = moradorRepository.findById(moradorId)
                .orElseThrow(() -> new IllegalArgumentException("Morador com ID " + moradorId + " não encontrado."));
        log.info("Buscando visitantes esperados para o morador {}", morador.getNome());
        return visitanteRepository.findByMoradorResponsavelAndStatusVisitaOrderByDataHoraVisitaPrevistaAsc(
                morador, "ESPERADO");
    }

    /**
     * Lista todos os visitantes registrados por um morador.
     */
    public List<Visitante> listarTodosVisitantesPorMorador(Long moradorId) {
        Morador morador = moradorRepository.findById(moradorId)
                .orElseThrow(() -> new IllegalArgumentException("Morador com ID " + moradorId + " não encontrado."));
        log.info("Buscando todos os visitantes do morador {}", morador.getNome());
        return visitanteRepository.findByMoradorResponsavelOrderByDataHoraVisitaPrevistaDesc(morador);
    }


    /**
     * Consulta o histórico de visitantes com base em filtros.
     * TODO: Implementar filtros mais robustos usando JpaSpecificationExecutor ou múltiplos métodos no repositório.
     */
    public List<Visitante> consultarHistoricoVisitantes(Long condominioId, Optional<String> nomeFiltro,
                                                        Optional<LocalDate> dataFiltro,
                                                        Optional<String> unidadeFiltro) {
        Condominium condominio = condominiumRepository.findById(condominioId)
                .orElseThrow(() -> new IllegalArgumentException("Condomínio com ID " + condominioId + " não encontrado."));

        log.info("Consultando histórico de visitantes para o condomínio {} com filtros: nome={}, data={}, unidade={}",
                condominio.getNome(), nomeFiltro, dataFiltro, unidadeFiltro);

        // Lógica de filtro simples (pode ser melhorada com Specifications)
        if (nomeFiltro.isPresent() && !nomeFiltro.get().isBlank()) {
            return visitanteRepository.findByCondominioAndNomeVisitanteContainingIgnoreCaseOrderByDataHoraVisitaPrevistaDesc(
                    condominio, nomeFiltro.get());
        }
        if (unidadeFiltro.isPresent() && !unidadeFiltro.get().isBlank()) {
            return visitanteRepository.findByCondominioAndUnidadeDestinoIgnoreCaseOrderByDataHoraVisitaPrevistaDesc(
                    condominio, unidadeFiltro.get());
        }
        if (dataFiltro.isPresent()) {
            LocalDateTime inicioDia = dataFiltro.get().atStartOfDay();
            LocalDateTime fimDia = dataFiltro.get().atTime(LocalTime.MAX);
            return visitanteRepository.findByCondominioAndDataHoraVisitaPrevistaBetweenOrderByDataHoraVisitaPrevistaDesc(
                    condominio, inicioDia, fimDia);
        }

        // Se nenhum filtro específico for aplicado, retorna todos do condomínio
        return visitanteRepository.findByCondominioOrderByDataHoraVisitaPrevistaDesc(condominio);
    }

    public List<Visitante> listarVisitantesPresentes(Long condominioId) {
        Condominium condominio = condominiumRepository.findById(condominioId)
                .orElseThrow(() -> new IllegalArgumentException("Condomínio com ID " + condominioId + " não encontrado."));

        log.info("Buscando visitantes com status 'CHEGOU' no condomínio {}", condominio.getNome());
        return visitanteRepository.findByCondominioAndStatusVisitaOrderByDataHoraEntradaEfetivaDesc(
                condominio, "CHEGOU");
    }

    // Métodos para registrar entrada e saída do visitante (para Funcionário/Portaria)
    @Transactional
    public Visitante registrarEntradaVisitante(Long visitanteId) {
        Visitante visitante = visitanteRepository.findById(visitanteId)
                .orElseThrow(() -> new IllegalArgumentException("Visitante com ID " + visitanteId + " não encontrado."));

        if (!"ESPERADO".equals(visitante.getStatusVisita()) && !"AUTORIZADO".equals(visitante.getStatusVisita())) { // Supondo um status "AUTORIZADO"
            throw new IllegalStateException("Visitante não está com status ESPERADO ou AUTORIZADO. Status atual: " + visitante.getStatusVisita());
        }

        visitante.setStatusVisita("CHEGOU");
        visitante.setDataHoraEntradaEfetiva(LocalDateTime.now());
        log.info("Registrada entrada para visitante ID {}", visitanteId);
        return visitanteRepository.save(visitante);
    }

    @Transactional
    public Visitante registrarSaidaVisitante(Long visitanteId) {
        Visitante visitante = visitanteRepository.findById(visitanteId)
                .orElseThrow(() -> new IllegalArgumentException("Visitante com ID " + visitanteId + " não encontrado."));

        if (!"CHEGOU".equals(visitante.getStatusVisita())) {
            throw new IllegalStateException("Visitante não está com status CHEGOU para registrar saída. Status atual: " + visitante.getStatusVisita());
        }

        visitante.setStatusVisita("SAIU");
        visitante.setDataHoraSaidaEfetiva(LocalDateTime.now());
        log.info("Registrada saída para visitante ID {}", visitanteId);
        return visitanteRepository.save(visitante);
    }
}