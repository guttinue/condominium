package com.condo.service;

import com.condo.domain.Condominium;
import com.condo.domain.Morador;
import com.condo.domain.Visitante;
import com.condo.repository.CondominiumRepository;
import com.condo.repository.MoradorRepository;
import com.condo.repository.VisitanteRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class VisitanteServiceTest {

    @Mock
    private VisitanteRepository visitanteRepository;

    @Mock
    private MoradorRepository moradorRepository;

    @Mock
    private CondominiumRepository condominiumRepository;

    @InjectMocks
    private VisitanteService visitanteService;

    private Morador moradorMock;
    private Condominium condominioMock;
    private Visitante visitanteMock;
    private LocalDateTime dataHoraPrevistaValida;

    @BeforeEach
    void setUp() {
        condominioMock = new Condominium();
        condominioMock.setId(1L);
        condominioMock.setNome("Condomínio Alpha");

        moradorMock = new Morador();
        moradorMock.setId(1L);
        moradorMock.setNome("João Residente");
        moradorMock.setCondominio(condominioMock);
        moradorMock.setUnidade("Apt 101");

        dataHoraPrevistaValida = LocalDateTime.now().plusDays(1); // Visita para amanhã

        visitanteMock = new Visitante(
                "Carlos Visitante",
                dataHoraPrevistaValida,
                "Apt 101",
                moradorMock,
                condominioMock,
                "123456789",
                "BRA-1234"
        );
        visitanteMock.setId(1L);
        visitanteMock.setStatusVisita("ESPERADO");
    }

    @Test
    void registrarVisitante_ComDadosValidos_DeveSalvarERetornarVisitante() {
        when(moradorRepository.findById(1L)).thenReturn(Optional.of(moradorMock));
        when(condominiumRepository.findById(1L)).thenReturn(Optional.of(condominioMock));
        when(visitanteRepository.save(any(Visitante.class))).thenAnswer(invocation -> {
            Visitante v = invocation.getArgument(0);
            v.setId(2L); // Simula ID gerado
            return v;
        });

        Visitante resultado = visitanteService.registrarVisitante(
                moradorMock.getId(), "Ana Visitante", dataHoraPrevistaValida,
                moradorMock.getUnidade(), "987654321", "XYZ-9876", condominioMock.getId()
        );

        assertNotNull(resultado);
        assertEquals(2L, resultado.getId());
        assertEquals("Ana Visitante", resultado.getNomeVisitante());
        assertEquals(moradorMock, resultado.getMoradorResponsavel());
        assertEquals(condominioMock, resultado.getCondominio());
        assertEquals("ESPERADO", resultado.getStatusVisita());
        verify(visitanteRepository, times(1)).save(any(Visitante.class));
    }

    @Test
    void registrarVisitante_QuandoMoradorNaoEncontrado_DeveLancarExcecao() {
        when(moradorRepository.findById(99L)).thenReturn(Optional.empty());

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            visitanteService.registrarVisitante(99L, "Nome Visitante", dataHoraPrevistaValida, "Unidade", "Doc", "Placa", 1L);
        });
        assertTrue(exception.getMessage().contains("Morador responsável com ID 99 não encontrado."));
        verify(visitanteRepository, never()).save(any(Visitante.class));
    }

    @Test
    void registrarVisitante_QuandoCondominioNaoEncontrado_DeveLancarExcecao() {
        when(moradorRepository.findById(1L)).thenReturn(Optional.of(moradorMock));
        when(condominiumRepository.findById(99L)).thenReturn(Optional.empty());

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            visitanteService.registrarVisitante(1L, "Nome Visitante", dataHoraPrevistaValida, "Unidade", "Doc", "Placa", 99L);
        });
        assertTrue(exception.getMessage().contains("Condomínio com ID 99 não encontrado."));
        verify(visitanteRepository, never()).save(any(Visitante.class));
    }


    @Test
    void registrarVisitante_ComNomeVazio_DeveLancarExcecao() {
        when(moradorRepository.findById(1L)).thenReturn(Optional.of(moradorMock));
        when(condominiumRepository.findById(1L)).thenReturn(Optional.of(condominioMock));

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            visitanteService.registrarVisitante(1L, " ", dataHoraPrevistaValida, "A101", null, null, 1L);
        });
        assertEquals("Nome do visitante é obrigatório.", exception.getMessage());
    }

    @Test
    void registrarVisitante_ComDataNoPassado_DeveLancarExcecao() {
        when(moradorRepository.findById(1L)).thenReturn(Optional.of(moradorMock));
        when(condominiumRepository.findById(1L)).thenReturn(Optional.of(condominioMock));
        LocalDateTime dataPassada = LocalDateTime.now().minusDays(1);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            visitanteService.registrarVisitante(1L, "Visitante", dataPassada, "A101", null, null, 1L);
        });
        assertEquals("Data/hora da visita prevista inválida ou no passado.", exception.getMessage());
    }


    @Test
    void listarVisitantesEsperadosHoje_QuandoExistem_DeveRetornarLista() {
        when(condominiumRepository.findById(1L)).thenReturn(Optional.of(condominioMock));
        List<Visitante> listaSimulada = List.of(visitanteMock);
        LocalDateTime inicioHoje = LocalDate.now().atStartOfDay();
        LocalDateTime fimHoje = LocalDate.now().atTime(LocalTime.MAX);

        when(visitanteRepository.findByCondominioAndStatusVisitaAndDataHoraVisitaPrevistaBetween(
                condominioMock, "ESPERADO", inicioHoje, fimHoje
        )).thenReturn(listaSimulada);

        List<Visitante> resultado = visitanteService.listarVisitantesEsperadosHoje(1L);

        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        assertEquals("Carlos Visitante", resultado.get(0).getNomeVisitante());
    }

    @Test
    void listarVisitantesEsperadosPorMorador_QuandoExistem_DeveRetornarLista() {
        when(moradorRepository.findById(1L)).thenReturn(Optional.of(moradorMock));
        List<Visitante> listaSimulada = List.of(visitanteMock);
        when(visitanteRepository.findByMoradorResponsavelAndStatusVisitaOrderByDataHoraVisitaPrevistaAsc(moradorMock, "ESPERADO"))
                .thenReturn(listaSimulada);

        List<Visitante> resultado = visitanteService.listarVisitantesEsperadosPorMorador(1L);

        assertNotNull(resultado);
        assertEquals(1, resultado.size());
    }

    @Test
    void listarTodosVisitantesPorMorador_QuandoExistem_DeveRetornarLista() {
        when(moradorRepository.findById(1L)).thenReturn(Optional.of(moradorMock));
        List<Visitante> listaSimulada = List.of(visitanteMock);
        when(visitanteRepository.findByMoradorResponsavelOrderByDataHoraVisitaPrevistaDesc(moradorMock))
                .thenReturn(listaSimulada);

        List<Visitante> resultado = visitanteService.listarTodosVisitantesPorMorador(1L);

        assertNotNull(resultado);
        assertEquals(1, resultado.size());
    }


    @Test
    void consultarHistoricoVisitantes_SemFiltros_DeveRetornarTodosDoCondominio() {
        when(condominiumRepository.findById(1L)).thenReturn(Optional.of(condominioMock));
        List<Visitante> listaSimulada = List.of(visitanteMock);
        when(visitanteRepository.findByCondominioOrderByDataHoraVisitaPrevistaDesc(condominioMock)).thenReturn(listaSimulada);

        List<Visitante> resultado = visitanteService.consultarHistoricoVisitantes(1L, Optional.empty(), Optional.empty(), Optional.empty());

        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        verify(visitanteRepository, times(1)).findByCondominioOrderByDataHoraVisitaPrevistaDesc(condominioMock);
    }

    @Test
    void registrarEntradaVisitante_ComStatusEsperado_DeveAtualizarStatusParaChegou() {
        visitanteMock.setStatusVisita("ESPERADO"); // Garante o estado inicial
        when(visitanteRepository.findById(1L)).thenReturn(Optional.of(visitanteMock));
        when(visitanteRepository.save(any(Visitante.class))).thenReturn(visitanteMock);

        Visitante resultado = visitanteService.registrarEntradaVisitante(1L);

        assertNotNull(resultado);
        assertEquals("CHEGOU", resultado.getStatusVisita());
        assertNotNull(resultado.getDataHoraEntradaEfetiva());
        verify(visitanteRepository, times(1)).save(visitanteMock);
    }

    @Test
    void registrarEntradaVisitante_ComStatusInvalido_DeveLancarExcecao() {
        visitanteMock.setStatusVisita("SAIU");
        when(visitanteRepository.findById(1L)).thenReturn(Optional.of(visitanteMock));

        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> {
            visitanteService.registrarEntradaVisitante(1L);
        });
        assertTrue(exception.getMessage().contains("Visitante não está com status ESPERADO ou AUTORIZADO"));
    }

    @Test
    void registrarSaidaVisitante_ComStatusChegou_DeveAtualizarStatusParaSaiu() {
        visitanteMock.setStatusVisita("CHEGOU"); // Garante o estado inicial
        visitanteMock.setDataHoraEntradaEfetiva(LocalDateTime.now().minusHours(1)); // Simula que já entrou
        when(visitanteRepository.findById(1L)).thenReturn(Optional.of(visitanteMock));
        when(visitanteRepository.save(any(Visitante.class))).thenReturn(visitanteMock);

        Visitante resultado = visitanteService.registrarSaidaVisitante(1L);

        assertNotNull(resultado);
        assertEquals("SAIU", resultado.getStatusVisita());
        assertNotNull(resultado.getDataHoraSaidaEfetiva());
        verify(visitanteRepository, times(1)).save(visitanteMock);
    }

    @Test
    void registrarSaidaVisitante_ComStatusInvalido_DeveLancarExcecao() {
        visitanteMock.setStatusVisita("ESPERADO"); // Status que não permite saída direta
        when(visitanteRepository.findById(1L)).thenReturn(Optional.of(visitanteMock));

        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> {
            visitanteService.registrarSaidaVisitante(1L);
        });
        assertTrue(exception.getMessage().contains("Visitante não está com status CHEGOU para registrar saída"));
    }
}