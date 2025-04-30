package com.condominium.service;

import com.condominium.model.Morador;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class CondominiumServiceTests {
    private CondominiumService service;

    @BeforeEach
    void setUp() {
        service = new CondominiumService("MeuCondo");
    }

    @Test
    void adicionarMorador_deveAparecerNaLista() {
        Morador m = service.adicionarMorador("Alice", "000.000.000-00", "alice@mail", "1111", "senha", "Apt 101");
        assertNotNull(m.getId());
        assertEquals(1, service.getCondominium().getMoradores().size());
        assertEquals("Alice", service.getCondominium().getMoradores().get(0).getNome());
    }

    @Test
    void listaVazia_antesDoCadastro() {
        assertTrue(service.getCondominium().getMoradores().isEmpty());
    }
}
