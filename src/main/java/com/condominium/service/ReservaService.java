package com.condominium.service;

import com.condominium.model.AreaComum;
import com.condominium.model.Reserva;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class ReservaService {
    private List<Reserva> reservas = new ArrayList<>();
    private Long reservaCounter = 1L;

    // Método para reservar uma área, verificando se o horário está disponível
    public Reserva reservarArea(AreaComum area, LocalDate data, LocalTime horaInicio, LocalTime horaFim) {
        if (isHorarioDisponivel(area, data, horaInicio, horaFim)) {
            Reserva reserva = new Reserva(reservaCounter++, area, data, horaInicio, horaFim, "Pendente");
            reservas.add(reserva);
            return reserva;
        } else {
            return null;
        }
    }

    public boolean isHorarioDisponivel(AreaComum area, LocalDate data, LocalTime horaInicio, LocalTime horaFim) {
        for (Reserva r : reservas) {
            if (r.getArea().getId().equals(area.getId()) && r.getData().equals(data)) {
                if (!(horaFim.isBefore(r.getHoraInicio()) || horaInicio.isAfter(r.getHoraFim()))) {
                    return false;
                }
            }
        }
        return true;
    }

    public List<Reserva> listarReservas() {
        return reservas;
    }
}
