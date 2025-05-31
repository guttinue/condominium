package com.condo.service;

import com.condo.domain.Morador;
import com.condo.domain.Role;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.test.mock.mockito.MockBean;

import com.condo.repository.UsuarioRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class UsuarioServiceIntegrationTest {

    @MockBean
    private CommandLineRunner commandLineRunner;

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private com.condo.repository.UsuarioRepository usuarioRepository;





    @Test
    void cadastrarEBuscarNovoMorador_DeveFuncionarCorretamente() {

        String nome = "Carlos Integrado";
        String email = "carlos.integrado@condo.com";
        String senha = "securepassword";
        String telefone = "21987654321";
        String cpf = "101.202.303-44";
        String unidade = "Bloco Z Apt 007";



        Morador moradorCadastrado = usuarioService.cadastrarNovoMorador(nome, email, senha, telefone, cpf, unidade );


        assertNotNull(moradorCadastrado, "Morador cadastrado não deve ser nulo.");
        assertNotNull(moradorCadastrado.getId(), "ID do morador cadastrado não deve ser nulo.");
        assertEquals(nome, moradorCadastrado.getNome(), "O nome do morador cadastrado deve ser o esperado.");
        assertEquals(email, moradorCadastrado.getEmail(), "O email do morador cadastrado deve ser o esperado.");
        assertTrue(moradorCadastrado.getRoles().contains(Role.MORADOR), "Morador cadastrado deve ter a role MORADOR.");


        Optional<Morador> moradorBuscadoOpt = usuarioService.buscarMoradorPorId(moradorCadastrado.getId());


        assertTrue(moradorBuscadoOpt.isPresent(), "Morador buscado deve ser encontrado no banco.");
        Morador moradorBuscado = moradorBuscadoOpt.get();
        assertEquals(nome, moradorBuscado.getNome(), "O nome do morador buscado deve corresponder ao cadastrado.");
        assertEquals(unidade, moradorBuscado.getUnidade(), "A unidade do morador buscado deve corresponder à cadastrada.");
    }

    @Test
    @Transactional
    void cadastrarMorador_ComEmailDuplicado_DeveLancarExcecao() {

        String emailExistente = "email.duplicado.integracao@condo.com";

        usuarioService.cadastrarNovoMorador("Usuario Original Integracao", emailExistente, "senha", "tel", "111000111", "un1INT" );


        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            usuarioService.cadastrarNovoMorador("Outro Usuario Integracao", emailExistente, "outrasenha", "tel2", "222000222", "un2INT" );
        });
        assertTrue(exception.getMessage().contains("já está cadastrado"), "Mensagem de exceção para email duplicado não foi a esperada.");
    }

    @Test
    @Transactional
    void atualizarMorador_Existente_DeveAtualizarDados() {

        String emailOriginal = "att.integracao@condo.com";

        Morador morador = usuarioService.cadastrarNovoMorador("Para Atualizar Int", emailOriginal, "pass", "tel", "777000777", "U7INT" );
        Long moradorId = morador.getId();

        String novoNome = "Nome Atualizado Integracao";
        String novoEmail = "email.novo.integracao@condo.com";
        String novoTelefone = "999990000";
        String novaUnidade = "Nova Unidade 77 INT";


        Morador moradorAtualizado = usuarioService.atualizarMorador(moradorId, novoNome, novoEmail, novoTelefone, novaUnidade);


        assertNotNull(moradorAtualizado, "Morador atualizado não deve ser nulo.");
        assertEquals(moradorId, moradorAtualizado.getId(), "ID do morador não deve mudar após atualização.");
        assertEquals(novoNome, moradorAtualizado.getNome(), "Nome não foi atualizado corretamente.");
        assertEquals(novoEmail, moradorAtualizado.getEmail(), "Email não foi atualizado corretamente.");
        assertEquals(novoTelefone, moradorAtualizado.getTelefone(), "Telefone não foi atualizado corretamente.");
        assertEquals(novaUnidade, moradorAtualizado.getUnidade(), "Unidade não foi atualizada corretamente.");


        Optional<Morador> moradorDoBanco = usuarioService.buscarMoradorPorId(moradorId);
        assertTrue(moradorDoBanco.isPresent(), "Morador atualizado não foi encontrado no banco na re-busca.");
        assertEquals(novoNome, moradorDoBanco.get().getNome(), "Nome no banco difere do nome atualizado.");
    }
}