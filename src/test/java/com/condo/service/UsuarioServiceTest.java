package com.condo.service;

import com.condo.domain.Morador;
import com.condo.domain.Role;
import com.condo.domain.Usuario;
import com.condo.repository.MoradorRepository;
import com.condo.repository.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;


import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UsuarioServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private MoradorRepository moradorRepository;



    @InjectMocks
    private UsuarioService usuarioService;

    private Morador moradorParaTeste;
    private Morador moradorSalvoSimulado;

    @BeforeEach
    void setUp() {
        moradorParaTeste = new Morador();

        moradorParaTeste.setNome("João Novo");
        moradorParaTeste.setEmail("joao.novo@condo.com");
        moradorParaTeste.setSenha("senha123");
        moradorParaTeste.setCpf("123.456.789-00");
        moradorParaTeste.setUnidade("Apt 101");


        moradorSalvoSimulado = new Morador();
        moradorSalvoSimulado.setId(1L);
        moradorSalvoSimulado.setNome("João Novo");
        moradorSalvoSimulado.setEmail("joao.novo@condo.com");
        moradorSalvoSimulado.setSenha("senha123");
        moradorSalvoSimulado.setCpf("123.456.789-00");
        moradorSalvoSimulado.setUnidade("Apt 101");
        Set<Role> roles = new HashSet<>();
        roles.add(Role.MORADOR);
        moradorSalvoSimulado.setRoles(roles);
    }

    @Test
    void cadastrarNovoMorador_ComDadosValidos_DeveRetornarMoradorSalvo() {

        String nome = moradorParaTeste.getNome();
        String email = moradorParaTeste.getEmail();
        String senha = moradorParaTeste.getSenha();
        String telefone = "11988887777";
        String cpf = moradorParaTeste.getCpf();
        String unidade = moradorParaTeste.getUnidade();


        when(usuarioRepository.findByEmail(email)).thenReturn(Optional.empty());
        when(moradorRepository.findByCpf(cpf)).thenReturn(Optional.empty());




        when(moradorRepository.save(any(Morador.class))).thenAnswer(invocation -> {
            Morador mSalvo = invocation.getArgument(0);

            mSalvo.setId(1L);
            if (mSalvo.getRoles() == null || mSalvo.getRoles().isEmpty()) {
                Set<Role> roles = new HashSet<>();
                roles.add(Role.MORADOR);
                mSalvo.setRoles(roles);
            }
            return mSalvo;
        });


        Morador moradorSalvo = usuarioService.cadastrarNovoMorador(nome, email, senha, telefone, cpf, unidade );


        assertNotNull(moradorSalvo);
        assertNotNull(moradorSalvo.getId());
        assertEquals(nome, moradorSalvo.getNome());
        assertEquals(email, moradorSalvo.getEmail());

        assertEquals(senha, moradorSalvo.getSenha());
        assertTrue(moradorSalvo.getRoles().contains(Role.MORADOR));

        verify(moradorRepository, times(1)).save(any(Morador.class));
        verify(usuarioRepository, times(1)).findByEmail(email);
        verify(moradorRepository, times(1)).findByCpf(cpf);
    }

    @Test
    void cadastrarNovoMorador_ComEmailExistente_DeveLancarIllegalArgumentException() {

        String emailExistente = "existente@condo.com";
        Usuario usuarioExistente = new Usuario() {};
        usuarioExistente.setEmail(emailExistente);
        when(usuarioRepository.findByEmail(emailExistente)).thenReturn(Optional.of(usuarioExistente));


        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            usuarioService.cadastrarNovoMorador("Outro Nome", emailExistente, "senha", "tel", "cpfValido", "unidade");
        });
        assertEquals("O email '" + emailExistente + "' já está cadastrado no sistema.", exception.getMessage());
        verify(moradorRepository, never()).save(any(Morador.class));
    }

    @Test
    void atualizarMorador_ComSucesso() {

        Long moradorId = 1L;
        Morador moradorExistente = new Morador();
        moradorExistente.setId(moradorId);
        moradorExistente.setNome("Nome Antigo");
        moradorExistente.setEmail("antigo@condo.com");
        moradorExistente.setTelefone("12345");
        moradorExistente.setUnidade("U1");

        String novoNome = "Nome Novo";
        String novoEmail = "novo@condo.com";
        String novoTelefone = "54321";
        String novaUnidade = "U2";

        when(moradorRepository.findById(moradorId)).thenReturn(Optional.of(moradorExistente));

        when(usuarioRepository.findByEmail(novoEmail)).thenReturn(Optional.empty());

        when(moradorRepository.save(any(Morador.class))).thenAnswer(invocation -> invocation.getArgument(0));



        Morador atualizado = usuarioService.atualizarMorador(moradorId, novoNome, novoEmail, novoTelefone, novaUnidade);


        assertNotNull(atualizado);
        assertEquals(novoNome, atualizado.getNome());
        assertEquals(novoEmail, atualizado.getEmail());
        assertEquals(novoTelefone, atualizado.getTelefone());
        assertEquals(novaUnidade, atualizado.getUnidade());
        verify(moradorRepository, times(1)).save(moradorExistente);
        verify(moradorRepository, times(1)).findById(moradorId);
        verify(usuarioRepository, times(1)).findByEmail(novoEmail);
    }

    @Test
    void atualizarMorador_QuandoMoradorNaoEncontrado_DeveLancarExcecao() {

        Long moradorIdInexistente = 99L;
        when(moradorRepository.findById(moradorIdInexistente)).thenReturn(Optional.empty());


        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            usuarioService.atualizarMorador(moradorIdInexistente, "Nome Novo", "email@novo.com", null, null);
        });
        assertTrue(exception.getMessage().contains("não encontrado"));
        verify(moradorRepository, never()).save(any(Morador.class));
    }
}