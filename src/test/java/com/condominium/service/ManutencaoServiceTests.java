package com.condominium.service;

import com.condominium.model.ReportManutencao;
import com.condominium.model.StatusManutencao;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ManutencaoServiceTests {

    private ManutencaoService service;

    @BeforeEach
    void setUp() {
        service = new ManutencaoService();
    }

    @Test
    void reportarProblema_deveCriarRelatorioComProtocolo() {
        ReportManutencao r = service.reportarProblema("Hall", "Luz queimada", false);
        assertNotNull(r.getProtocolo());
        assertEquals(StatusManutencao.PENDENTE, r.getStatus());
    }

    @Test
    void alterarStatus_deveAtualizarELogarHistorico() {
        ReportManutencao r = service.reportarProblema("Piscina", "Azulejo solto", true);
        boolean ok = service.alterarStatus(r.getId(), StatusManutencao.EM_ANDAMENTO);
        assertTrue(ok);
        assertEquals(StatusManutencao.EM_ANDAMENTO, r.getStatus());
        List<String> hist = r.getHistorico();
        assertTrue(hist.stream().anyMatch(l -> l.contains("EM_ANDAMENTO")));
    }
}
