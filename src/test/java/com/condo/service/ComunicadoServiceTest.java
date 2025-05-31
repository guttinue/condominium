package com.condo.service;

import com.condo.domain.Comunicado;
import com.condo.domain.Condominium;
import com.condo.domain.Morador;
import com.condo.domain.Role;
import com.condo.domain.Sindico;
import com.condo.domain.Usuario;
import com.condo.repository.ComunicadoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ComunicadoServiceTest {

    @Mock
    private ComunicadoRepository comunicadoRepository;


    @InjectMocks
    private ComunicadoService comunicadoService;

    private Sindico sindicoMock;
    private Condominium condominioMock;
    private Morador moradorMock;

    @BeforeEach
    void setUp() {
        condominioMock = new Condominium();
        condominioMock.setId(1L);
        condominioMock.setNome("Condomínio TestVille");

        sindicoMock = new Sindico();
        sindicoMock.setId(1L);
        sindicoMock.setNome("Síndico Teste Unitário");
        sindicoMock.setEmail("sindico.unit@condo.com");
        sindicoMock.setCondominio(condominioMock);
        Set<Role> rolesSindico = new HashSet<>();
        rolesSindico.add(Role.SINDICO);
        sindicoMock.setRoles(rolesSindico);


        moradorMock = new Morador();
        moradorMock.setId(2L);
        moradorMock.setNome("Morador Comum Teste");
        moradorMock.setEmail("morador.unit@condo.com");
        moradorMock.setCondominio(condominioMock);
        Set<Role> rolesMorador = new HashSet<>();
        rolesMorador.add(Role.MORADOR);
        moradorMock.setRoles(rolesMorador);
    }

    @Test
    void criarComunicado_ComDadosValidosESindico_DeveSalvarERetornarComunicado() {
        // Arrange
        String titulo = "Manutenção Piscina";
        String conteudo = "A piscina estará fechada para manutenção na próxima semana.";

        when(comunicadoRepository.save(any(Comunicado.class))).thenAnswer(invocation -> {
            Comunicado c = invocation.getArgument(0);
            c.setId(1L);
            c.setDataPublicacao(LocalDateTime.now());
            return c;
        });

        Comunicado resultado = comunicadoService.criarComunicado(titulo, conteudo, sindicoMock, condominioMock);

        assertNotNull(resultado);
        assertEquals(1L, resultado.getId());
        assertEquals(titulo, resultado.getTitulo());
        assertEquals(conteudo, resultado.getConteudo());
        assertEquals(sindicoMock, resultado.getSindico());
        assertEquals(condominioMock, resultado.getCondominio());
        assertNotNull(resultado.getDataPublicacao());

        verify(comunicadoRepository, times(1)).save(any(Comunicado.class));
    }

    @Test
    void criarComunicado_QuandoAutorNaoESindico_DeveLancarSecurityException() {
        String titulo = "Festa Junina";
        String conteudo = "Teremos festa junina!";

        SecurityException exception = assertThrows(SecurityException.class, () -> {
            comunicadoService.criarComunicado(titulo, conteudo, moradorMock, condominioMock);
        });

        assertEquals("Apenas síndicos podem criar comunicados.", exception.getMessage());
        verify(comunicadoRepository, never()).save(any(Comunicado.class));
    }

    @Test
    void criarComunicado_QuandoTituloVazio_DeveLancarIllegalArgumentException() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            comunicadoService.criarComunicado("", "Conteúdo válido", sindicoMock, condominioMock);
        });
        assertEquals("O título do comunicado não pode ser vazio.", exception.getMessage());
    }

    @Test
    void criarComunicado_QuandoConteudoVazio_DeveLancarIllegalArgumentException() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            comunicadoService.criarComunicado("Título Válido", "  ", sindicoMock, condominioMock);
        });
        assertEquals("O conteúdo do comunicado não pode ser vazio.", exception.getMessage());
    }

    @Test
    void criarComunicado_QuandoCondominioNulo_DeveLancarIllegalArgumentException() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            comunicadoService.criarComunicado("Título Válido", "Conteúdo", sindicoMock, null);
        });
        assertEquals("O condomínio associado ao comunicado não pode ser nulo.", exception.getMessage());
    }


    @Test
    void listarComunicadosPorCondominio_QuandoCondominioValidoEExistemComunicados_DeveRetornarLista() {
        List<Comunicado> listaSimulada = List.of(
                new Comunicado("Aviso 1", "Detalhes 1", sindicoMock, condominioMock),
                new Comunicado("Aviso 2", "Detalhes 2", sindicoMock, condominioMock)
        );
        when(comunicadoRepository.findByCondominioOrderByDataPublicacaoDesc(condominioMock)).thenReturn(listaSimulada);

        List<Comunicado> resultado = comunicadoService.listarComunicadosPorCondominio(condominioMock);

        assertNotNull(resultado);
        assertEquals(2, resultado.size());
        assertEquals("Aviso 1", resultado.get(0).getTitulo());
        verify(comunicadoRepository, times(1)).findByCondominioOrderByDataPublicacaoDesc(condominioMock);
    }

    @Test
    void listarComunicadosPorCondominio_QuandoCondominioNulo_DeveLancarIllegalArgumentException() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            comunicadoService.listarComunicadosPorCondominio(null);
        });
        assertEquals("Condomínio não pode ser nulo para listar comunicados.", exception.getMessage());
    }

    @Test
    void listarComunicadosPorCondominio_QuandoNaoExistemComunicados_DeveRetornarListaVazia() {
        when(comunicadoRepository.findByCondominioOrderByDataPublicacaoDesc(condominioMock)).thenReturn(Collections.emptyList());

        List<Comunicado> resultado = comunicadoService.listarComunicadosPorCondominio(condominioMock);

        assertNotNull(resultado);
        assertTrue(resultado.isEmpty());
    }

    @Test
    void listarComunicadosPorSindico_QuandoSindicoValido_DeveRetornarLista() {
        List<Comunicado> listaSimulada = List.of(
                new Comunicado("Meu Aviso 1", "Detalhes S1", sindicoMock, condominioMock)
        );
        when(comunicadoRepository.findBySindicoOrderByDataPublicacaoDesc(sindicoMock)).thenReturn(listaSimulada);

        List<Comunicado> resultado = comunicadoService.listarComunicadosPorSindico(sindicoMock);

        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        assertEquals("Meu Aviso 1", resultado.get(0).getTitulo());
        verify(comunicadoRepository, times(1)).findBySindicoOrderByDataPublicacaoDesc(sindicoMock);
    }

    @Test
    void listarComunicadosPorSindico_QuandoUsuarioNaoESindico_DeveLancarSecurityException() {
        SecurityException exception = assertThrows(SecurityException.class, () -> {
            comunicadoService.listarComunicadosPorSindico(moradorMock);
        });
        assertEquals("Apenas síndicos podem listar os comunicados que eles próprios emitiram desta forma.", exception.getMessage());
    }
}