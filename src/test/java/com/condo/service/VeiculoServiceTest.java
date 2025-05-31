package com.condo.service;

import com.condo.domain.Condominium;
import com.condo.domain.Morador;
import com.condo.domain.Veiculo;
import com.condo.repository.CondominiumRepository;
import com.condo.repository.MoradorRepository;
import com.condo.repository.VeiculoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class VeiculoServiceTest {

    @Mock
    private VeiculoRepository veiculoRepository;

    @Mock
    private MoradorRepository moradorRepository;

    @Mock
    private CondominiumRepository condominiumRepository;

    @InjectMocks
    private VeiculoService veiculoService;

    private Morador moradorMock;
    private Condominium condominioMock;
    private Veiculo veiculoMock;

    @BeforeEach
    void setUp() {
        condominioMock = new Condominium();
        condominioMock.setId(1L);
        condominioMock.setNome("Condomínio Principal");

        moradorMock = new Morador();
        moradorMock.setId(1L);
        moradorMock.setNome("João Morador");
        moradorMock.setEmail("joao@condo.com");
        moradorMock.setCondominio(condominioMock); // Importante para a lógica do service

        veiculoMock = new Veiculo("ABC-1234", "Honda Civic", "Preto", "CARRO", moradorMock, condominioMock);
        veiculoMock.setId(1L);
    }

    @Test
    void cadastrarVeiculo_ComDadosValidos_DeveSalvarERetornarVeiculo() {
        when(moradorRepository.findById(1L)).thenReturn(Optional.of(moradorMock));
        when(veiculoRepository.existsByPlacaAndCondominio(anyString(), any(Condominium.class))).thenReturn(false);
        when(veiculoRepository.save(any(Veiculo.class))).thenAnswer(invocation -> {
            Veiculo v = invocation.getArgument(0);
            v.setId(2L);
            return v;
        });

        Veiculo resultado = veiculoService.cadastrarVeiculo(1L, "DEF-5678", "Toyota Corolla", "Branco", "CARRO");

        assertNotNull(resultado);
        assertEquals(2L, resultado.getId());
        assertEquals("DEF-5678", resultado.getPlaca());
        assertEquals("Toyota Corolla", resultado.getModelo());
        assertEquals(moradorMock, resultado.getMorador());
        assertEquals(condominioMock, resultado.getCondominio());
        verify(veiculoRepository, times(1)).save(any(Veiculo.class));
    }

    @Test
    void cadastrarVeiculo_QuandoMoradorNaoEncontrado_DeveLancarExcecao() {
        when(moradorRepository.findById(99L)).thenReturn(Optional.empty());

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            veiculoService.cadastrarVeiculo(99L, "XYZ-1234", "Modelo", "Cor", "TIPO");
        });
        assertEquals("Morador com ID 99 não encontrado.", exception.getMessage());
        verify(veiculoRepository, never()).save(any(Veiculo.class));
    }

    @Test
    void cadastrarVeiculo_QuandoMoradorNaoTemCondominio_DeveLancarExcecao() {
        moradorMock.setCondominio(null); // Morador sem condomínio
        when(moradorRepository.findById(1L)).thenReturn(Optional.of(moradorMock));

        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> {
            veiculoService.cadastrarVeiculo(1L, "XYZ-1234", "Modelo", "Cor", "TIPO");
        });
        assertEquals("Morador não está associado a um condomínio. Não é possível cadastrar veículo.", exception.getMessage());
    }

    @Test
    void cadastrarVeiculo_ComPlacaJaExistenteNoCondominio_DeveLancarExcecao() {
        when(moradorRepository.findById(1L)).thenReturn(Optional.of(moradorMock));
        when(veiculoRepository.existsByPlacaAndCondominio("ABC-1234", condominioMock)).thenReturn(true);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            veiculoService.cadastrarVeiculo(1L, "ABC-1234", "Outro Modelo", "Azul", "MOTO");
        });
        assertEquals("Veículo com placa 'ABC-1234' já cadastrado neste condomínio.", exception.getMessage());
    }

    @Test
    void cadastrarVeiculo_ComModeloVazio_DeveLancarExcecao() {
        when(moradorRepository.findById(1L)).thenReturn(Optional.of(moradorMock));
        when(veiculoRepository.existsByPlacaAndCondominio(anyString(), any(Condominium.class))).thenReturn(false);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            veiculoService.cadastrarVeiculo(1L, "NEW-0000", " ", "Preto", "CARRO");
        });
        assertEquals("Modelo, cor e tipo do veículo são obrigatórios.", exception.getMessage());
    }


    @Test
    void listarVeiculosPorMorador_QuandoMoradorExiste_DeveRetornarListaDeVeiculos() {
        when(moradorRepository.findById(1L)).thenReturn(Optional.of(moradorMock));
        List<Veiculo> listaSimulada = List.of(veiculoMock);
        when(veiculoRepository.findByMoradorOrderByModeloAsc(moradorMock)).thenReturn(listaSimulada);

        List<Veiculo> resultado = veiculoService.listarVeiculosPorMorador(1L);

        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        assertEquals(veiculoMock.getPlaca(), resultado.get(0).getPlaca());
        verify(veiculoRepository, times(1)).findByMoradorOrderByModeloAsc(moradorMock);
    }

    @Test
    void listarVeiculosPorMorador_QuandoMoradorNaoEncontrado_DeveLancarExcecao() {
        when(moradorRepository.findById(99L)).thenReturn(Optional.empty());

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            veiculoService.listarVeiculosPorMorador(99L);
        });
        assertEquals("Morador com ID 99 não encontrado.", exception.getMessage());
    }

    @Test
    void removerVeiculo_ComSucesso_QuandoVeiculoPertenceAoMorador() {
        when(moradorRepository.findById(1L)).thenReturn(Optional.of(moradorMock));
        when(veiculoRepository.findByIdAndMorador(1L, moradorMock)).thenReturn(Optional.of(veiculoMock));
        doNothing().when(veiculoRepository).delete(veiculoMock); // Configura o mock para não fazer nada no delete

        assertDoesNotThrow(() -> veiculoService.removerVeiculo(1L, 1L));

        verify(veiculoRepository, times(1)).delete(veiculoMock);
    }

    @Test
    void removerVeiculo_QuandoVeiculoNaoEncontradoOuNaoPertenceAoMorador_DeveLancarExcecao() {
        when(moradorRepository.findById(1L)).thenReturn(Optional.of(moradorMock));
        when(veiculoRepository.findByIdAndMorador(99L, moradorMock)).thenReturn(Optional.empty());

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            veiculoService.removerVeiculo(99L, 1L);
        });
        assertEquals("Veículo com ID 99 não encontrado ou não pertence a este morador.", exception.getMessage());
        verify(veiculoRepository, never()).delete(any(Veiculo.class));
    }

    @Test
    void listarTodosVeiculosPorCondominio_QuandoCondominioExiste_DeveRetornarLista() {
        when(condominiumRepository.findById(1L)).thenReturn(Optional.of(condominioMock));
        List<Veiculo> listaSimulada = List.of(veiculoMock);
        when(veiculoRepository.findByCondominioOrderByMorador_UnidadeAscPlacaAsc(condominioMock)).thenReturn(listaSimulada);

        List<Veiculo> resultado = veiculoService.listarTodosVeiculosPorCondominio(1L);

        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        verify(veiculoRepository, times(1)).findByCondominioOrderByMorador_UnidadeAscPlacaAsc(condominioMock);
    }

    @Test
    void listarTodosVeiculosPorCondominio_QuandoCondominioNaoEncontrado_DeveLancarExcecao() {
        when(condominiumRepository.findById(99L)).thenReturn(Optional.empty());

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            veiculoService.listarTodosVeiculosPorCondominio(99L);
        });
        assertEquals("Condomínio com ID 99 não encontrado.", exception.getMessage());
    }
}