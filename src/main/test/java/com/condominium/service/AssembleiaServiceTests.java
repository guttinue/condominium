package com.condominium.service;

import com.condominium.model.Assembleia;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class AssembleiaServiceTests {

    private AssembleiaService service;

    @BeforeEach
    void setUp() {
        service = new AssembleiaService();
    }

    @Test
    void agendarAssembleia_deveAparecerNaLista() {
        Assembleia a = service.agendarAssembleia(
                LocalDate.of(2025, 6, 15),
                LocalTime.of(19, 0),
                "Salão",
                "Prestação de Contas",
                30
        );
        assertNotNull(a.getId());
        List<Assembleia> lista = service.listarAssembleias();
        assertEquals(1, lista.size());
        assertEquals("Salão", lista.get(0).getLocal());
    }

    @Test
    void atualizarAssembleia_deveMudarPauta() {
        Assembleia a = service.agendarAssembleia(
                LocalDate.now(), LocalTime.now(), "Sala", "Pauta1", 10
        );
        boolean ok = service.atualizarAssembleia(a.getId(), a.getData(), a.getHorario(), "Nova Pauta");
        assertTrue(ok);
        assertEquals("Nova Pauta", service.buscarPorId(a.getId()).get().getPauta());
    }
}
