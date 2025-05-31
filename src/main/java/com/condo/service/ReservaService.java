package com.condo.service; // Confirme se este é o seu pacote

import com.condo.domain.AreaComum;
import com.condo.domain.Morador;
import com.condo.domain.Reserva;
import com.condo.domain.Sindico;
import com.condo.repository.AreaComumRepository;
import com.condo.repository.ReservaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
public class ReservaService {

    private final ReservaRepository reservaRepository;
    private final AreaComumRepository areaComumRepository;

    @Autowired
    public ReservaService(ReservaRepository reservaRepository, AreaComumRepository areaComumRepository) {
        this.reservaRepository = reservaRepository;
        this.areaComumRepository = areaComumRepository;
    }

    // --- Métodos para Áreas Comuns (chamados pelo MoradorMenu) ---

    /**
     * Lista todas as áreas comuns disponíveis.
     * @return Uma lista de todas as áreas comuns.
     */
    public List<AreaComum> listarAreasComuns() {
        return areaComumRepository.findAll();
    }

    /**
     * Busca uma área comum específica pelo seu ID.
     * @param id O ID da área comum.
     * @return Um Optional contendo a AreaComum se encontrada, ou vazio caso contrário.
     */
    public Optional<AreaComum> buscarAreaComumPorId(Long id) {
        return areaComumRepository.findById(id);
    }

    // --- Métodos de Gerenciamento de Reservas (usados por MoradorMenu e SindicoMenu) ---

    public List<Reserva> listarTodasAsReservas() {
        return reservaRepository.findAll();
    }

    public List<Reserva> listarReservasPorStatus(String status) {
        if (status == null || status.trim().isEmpty()) {
            System.out.println("Status para busca de reservas não pode ser vazio.");
            return Collections.emptyList();
        }
        return reservaRepository.findByStatus(status.toUpperCase());
    }

    /**
     * Lista todas as reservas feitas por um morador específico.
     * @param morador O morador.
     * @return Lista de reservas do morador.
     */
    public List<Reserva> listarReservasPorMorador(Morador morador) {
        if (morador == null) {
            System.out.println("Morador não pode ser nulo para listar suas reservas.");
            return Collections.emptyList();
        }
        return reservaRepository.findByMorador(morador);
    }

    @Transactional
    public Reserva aprovarReserva(Long reservaId, Sindico sindicoAprovador) {
        Reserva reserva = reservaRepository.findById(reservaId)
                .orElseThrow(() -> new IllegalArgumentException("Reserva com ID " + reservaId + " não encontrada."));

        if (!"PENDENTE".equalsIgnoreCase(reserva.getStatus())) {
            throw new IllegalStateException("Somente reservas com status PENDENTE podem ser aprovadas. Status atual: " + reserva.getStatus());
        }
        reserva.setStatus("APROVADA");
        return reservaRepository.save(reserva);
    }

    @Transactional
    public Reserva rejeitarReserva(Long reservaId, Sindico sindicoRejeitador, String motivo) {
        Reserva reserva = reservaRepository.findById(reservaId)
                .orElseThrow(() -> new IllegalArgumentException("Reserva com ID " + reservaId + " não encontrada."));

        if (!"PENDENTE".equalsIgnoreCase(reserva.getStatus())) {
            throw new IllegalStateException("Somente reservas com status PENDENTE podem ser rejeitadas. Status atual: " + reserva.getStatus());
        }
        reserva.setStatus("REJEITADA");
        // Adicionar lógica para motivo se necessário na entidade Reserva
        return reservaRepository.save(reserva);
    }

    @Transactional
    public Reserva criarReserva(Morador morador, Long areaComumId, LocalDate data, LocalTime horaInicio, LocalTime horaFim) {
        AreaComum areaComum = areaComumRepository.findById(areaComumId)
                .orElseThrow(() -> new IllegalArgumentException("Área comum com ID " + areaComumId + " não encontrada."));

        if (morador == null) {
            throw new IllegalArgumentException("Morador não pode ser nulo para criar uma reserva.");
        }
        if (data.isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("Não é possível fazer reservas para datas passadas.");
        }
        if (horaInicio.isAfter(horaFim) || horaInicio.equals(horaFim)) {
            throw new IllegalArgumentException("A hora de início deve ser anterior à hora de fim.");
        }

        // TODO: Implementar lógica robusta para verificar conflitos de horário.
        // Exemplo:
        // List<Reserva> conflitos = reservaRepository.findByAreaComumAndDataAndHorarioConflitante(areaComum, data, horaInicio, horaFim);
        // if (!conflitos.isEmpty()) {
        //     throw new IllegalStateException("Já existe uma reserva para esta área neste horário.");
        // }

        Reserva novaReserva = new Reserva();
        novaReserva.setMorador(morador);
        novaReserva.setAreaComum(areaComum);
        novaReserva.setData(data);
        novaReserva.setHoraInicio(horaInicio);
        novaReserva.setHoraFim(horaFim);
        novaReserva.setStatus("PENDENTE");
        novaReserva.setDataSolicitacao(LocalDateTime.now());

        return reservaRepository.save(novaReserva);
    }

    /**
     * Cancela uma reserva feita por um morador.
     * @param reservaId O ID da reserva a ser cancelada.
     * @param moradorSolicitante O morador que está solicitando o cancelamento.
     * @return true se a reserva foi cancelada, false caso contrário (embora exceções sejam preferíveis).
     * @throws IllegalArgumentException Se a reserva não for encontrada.
     * @throws IllegalStateException Se a reserva não puder ser cancelada (ex: status não permite).
     * @throws SecurityException Se o morador não for o dono da reserva.
     */
    @Transactional
    public boolean cancelarReserva(Long reservaId, Morador moradorSolicitante) {
        Reserva reserva = reservaRepository.findById(reservaId)
                .orElseThrow(() -> new IllegalArgumentException("Reserva com ID " + reservaId + " não encontrada."));

        // Verifica se o morador que está tentando cancelar é o mesmo que fez a reserva
        if (!reserva.getMorador().getId().equals(moradorSolicitante.getId())) {
            throw new SecurityException("Você não tem permissão para cancelar esta reserva.");
        }

        // Define quais status permitem cancelamento
        if ("PENDENTE".equalsIgnoreCase(reserva.getStatus()) || "APROVADA".equalsIgnoreCase(reserva.getStatus())) {
            reserva.setStatus("CANCELADA_PELO_MORADOR");
            // Você pode adicionar um campo dataCancelamento se quiser
            // reserva.setDataCancelamento(LocalDateTime.now());
            reservaRepository.save(reserva);
            return true;
        } else {
            throw new IllegalStateException("Esta reserva não pode ser cancelada pois seu status é: " + reserva.getStatus());
        }
    }

    public Optional<Reserva> buscarPorId(Long id) {
        return reservaRepository.findById(id);
    }
}