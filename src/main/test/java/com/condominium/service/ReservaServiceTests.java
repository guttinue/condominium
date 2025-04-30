package com.condominium.service;

import com.condominium.model.AreaComum;
import com.condominium.model.Reserva;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.*;

class ReservaServiceTests {

    private ReservaService service;
    private AreaComum quadra;

    @BeforeEach
    void setUp() {
        service = new ReservaService();
        quadra = new AreaComum(1L, "Quadra", "Esporte");
    }

    @Test
    void reservaHorarioLivre_deveCriar() {
        LocalDate d = LocalDate.of(2025, 5, 1);
        Reserva r = service.reservarArea(quadra, d, LocalTime.of(9, 0), LocalTime.of(11, 0));
        assertNotNull(r);
        assertEquals("Pendente", r.getStatus());
    }

    @Test
    void reservaSobreposta_deveRetornarNull() {
        LocalDate d = LocalDate.of(2025, 5, 1);
        service.reservarArea(quadra, d, LocalTime.of(9, 0), LocalTime.of(11, 0));
        Reserva r2 = service.reservarArea(quadra, d, LocalTime.of(10, 0), LocalTime.of(12, 0));
        assertNull(r2);
    }
}
