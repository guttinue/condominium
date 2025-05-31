// src/main/java/com/condo/service/ReservaService.java
package com.condo.service;

import com.condo.domain.AreaComum;
import com.condo.domain.Condominium;
import com.condo.domain.Morador;
import com.condo.domain.Reserva;
import com.condo.domain.Sindico;
import com.condo.repository.AreaComumRepository;
import com.condo.repository.MoradorRepository;   // 1. VERIFIQUE ESTE IMPORT
import com.condo.repository.ReservaRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ReservaService {

    private static final Logger log = LoggerFactory.getLogger(ReservaService.class);

    private final ReservaRepository reservaRepository;
    private final AreaComumRepository areaComumRepository;
    private final MoradorRepository moradorRepository; // 2. VERIFIQUE A DECLARAÇÃO DO CAMPO

    @Autowired
    public ReservaService(ReservaRepository reservaRepository,
                          AreaComumRepository areaComumRepository,
                          MoradorRepository moradorRepository) { // 3. VERIFIQUE O PARÂMETRO NO CONSTRUTOR
        this.reservaRepository = reservaRepository;
        this.areaComumRepository = areaComumRepository;
        this.moradorRepository = moradorRepository; // 4. VERIFIQUE A ATRIBUIÇÃO NO CONSTRUTOR
    }

    // Restante dos seus métodos do ReservaService...

    public List<AreaComum> listarAreasComuns() {
        return areaComumRepository.findAll();
    }

    public Optional<AreaComum> buscarAreaComumPorId(Long id) {
        return areaComumRepository.findById(id);
    }

    public boolean verificarDisponibilidade(AreaComum areaComum, LocalDate data, LocalTime horaInicio, LocalTime horaFim) {
        List<Reserva> reservasExistentes = reservaRepository.findByAreaComumAndData(areaComum, data);
        for (Reserva existente : reservasExistentes) {
            if ("APROVADA".equalsIgnoreCase(existente.getStatus()) || "PENDENTE".equalsIgnoreCase(existente.getStatus())) {
                if (horaInicio.isBefore(existente.getHoraFim()) && horaFim.isAfter(existente.getHoraInicio())) {
                    return false;
                }
            }
        }
        return true;
    }

    @Transactional
    public Reserva criarReserva(Morador morador, Long areaComumId, LocalDate data, LocalTime horaInicio, LocalTime horaFim) {
        AreaComum areaComum = areaComumRepository.findById(areaComumId)
                .orElseThrow(() -> new IllegalArgumentException("Área comum não encontrada com ID: " + areaComumId));

        // Exemplo de onde moradorRepository poderia ser usado (embora não seja estritamente necessário aqui
        // se o objeto Morador já vem completo e validado de quem chamou o serviço).
        // Se você precisasse buscar o morador por ID aqui:
        // Morador moradorPersistido = moradorRepository.findById(morador.getId())
        //       .orElseThrow(() -> new IllegalArgumentException("Morador não encontrado com ID: " + morador.getId()));


        if (morador.getCondominio() == null || areaComum.getCondominio() == null || !morador.getCondominio().getId().equals(areaComum.getCondominio().getId())) {
            throw new IllegalArgumentException("Morador não pertence ao mesmo condomínio da área comum solicitada.");
        }


        if (data.isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("A data da reserva não pode ser no passado.");
        }
        if (horaInicio.isAfter(horaFim) || horaInicio.equals(horaFim)) {
            throw new IllegalArgumentException("A hora de início deve ser anterior à hora de término.");
        }

        if (!verificarDisponibilidade(areaComum, data, horaInicio, horaFim)) {
            throw new IllegalStateException("Horário indisponível para a área " + areaComum.getNome() + " na data " + data);
        }

        Reserva novaReserva = new Reserva(areaComum, morador, data, horaInicio, horaFim, "PENDENTE");
        log.info("Morador {} solicitou reserva para {} em {} das {} às {}", morador.getNome(), areaComum.getNome(), data, horaInicio, horaFim);
        return reservaRepository.save(novaReserva);
    }

    public List<Reserva> listarReservasPorMorador(Morador morador) {
        return reservaRepository.findByMorador(morador);
    }

    @Transactional
    public boolean cancelarReserva(Long reservaId, Morador morador) {
        Reserva reserva = reservaRepository.findById(reservaId)
                .orElseThrow(() -> new IllegalArgumentException("Reserva não encontrada com ID: " + reservaId));

        if (!reserva.getMorador().getId().equals(morador.getId())) {
            throw new SecurityException("Morador não autorizado a cancelar esta reserva.");
        }
        if (!("PENDENTE".equalsIgnoreCase(reserva.getStatus()) || "APROVADA".equalsIgnoreCase(reserva.getStatus()))) {
            throw new IllegalStateException("Apenas reservas PENDENTES ou APROVADAS podem ser canceladas. Status atual: " + reserva.getStatus());
        }
        if (reserva.getData().isBefore(LocalDate.now().plusDays(1)) && "APROVADA".equalsIgnoreCase(reserva.getStatus())) {
            throw new IllegalStateException("Não é possível cancelar uma reserva APROVADA com menos de 24 horas de antecedência.");
        }
        reserva.setStatus("CANCELADA_PELO_MORADOR");
        log.info("Reserva ID {} cancelada pelo morador {}", reservaId, morador.getNome());
        reservaRepository.save(reserva);
        return true;
    }

    @Transactional(readOnly = true)
    public List<Reserva> listarTodasReservasPorCondominio(Condominium condominio) {
        if (condominio == null) {
            log.warn("Tentativa de listar todas as reservas para um condomínio nulo.");
            throw new IllegalArgumentException("Condomínio não pode ser nulo.");
        }
        log.info("Listando todas as reservas para o condomínio ID: {}", condominio.getId());
        return reservaRepository.findAllByCondominio(condominio);
    }

    @Transactional(readOnly = true)
    public List<Reserva> listarReservasPendentesPorCondominioEStatus(Condominium condominio, String status) {
        if (condominio == null) {
            log.warn("Tentativa de listar reservas pendentes para um condomínio nulo.");
            throw new IllegalArgumentException("Condomínio não pode ser nulo.");
        }
        if (status == null || status.trim().isEmpty()) {
            log.warn("Tentativa de listar reservas por condomínio com status vazio.");
            throw new IllegalArgumentException("Status não pode ser vazio.");
        }
        String statusUpper = status.toUpperCase();
        log.info("Listando reservas com status '{}' para o condomínio ID: {}", statusUpper, condominio.getId());
        return reservaRepository.findAllByCondominioAndStatus(condominio, statusUpper);
    }

    @Transactional
    public Reserva aprovarReserva(Long reservaId, Sindico sindico) {
        Reserva reserva = reservaRepository.findById(reservaId)
                .orElseThrow(() -> new IllegalArgumentException("Reserva não encontrada com ID: " + reservaId));

        if (sindico.getCondominio() == null || reserva.getAreaComum().getCondominio() == null || !sindico.getCondominio().getId().equals(reserva.getAreaComum().getCondominio().getId())) {
            throw new SecurityException("Síndico não autorizado a gerenciar reservas deste condomínio.");
        }

        if (!"PENDENTE".equalsIgnoreCase(reserva.getStatus())) {
            throw new IllegalStateException("Apenas reservas com status PENDENTE podem ser aprovadas. Status atual: " + reserva.getStatus());
        }
        reserva.setStatus("APROVADA");
        log.info("Reserva ID {} (Morador: {}) aprovada pelo Síndico {}", reservaId, reserva.getMorador().getNome(), sindico.getNome());
        return reservaRepository.save(reserva);
    }

    @Transactional
    public Reserva rejeitarReserva(Long reservaId, Sindico sindico, String motivo) {
        Reserva reserva = reservaRepository.findById(reservaId)
                .orElseThrow(() -> new IllegalArgumentException("Reserva não encontrada com ID: " + reservaId));

        if (sindico.getCondominio() == null || reserva.getAreaComum().getCondominio() == null || !sindico.getCondominio().getId().equals(reserva.getAreaComum().getCondominio().getId())) {
            throw new SecurityException("Síndico não autorizado a gerenciar reservas deste condomínio.");
        }

        if (!"PENDENTE".equalsIgnoreCase(reserva.getStatus())) {
            throw new IllegalStateException("Apenas reservas com status PENDENTE podem ser rejeitadas. Status atual: " + reserva.getStatus());
        }
        reserva.setStatus("REJEITADA");
        log.info("Reserva ID {} (Morador: {}) rejeitada pelo Síndico {}. Motivo: {}",
                reservaId, reserva.getMorador().getNome(), sindico.getNome(), (motivo != null && !motivo.trim().isEmpty() ? motivo : "Não especificado"));
        return reservaRepository.save(reserva);
    }
}