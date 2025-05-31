package com.condo.repository;

import com.condo.domain.Condominium;
import com.condo.domain.Morador;
import com.condo.domain.Role;
import com.condo.domain.Usuario;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class UsuarioRepositoryTest {
    
    @MockBean
    private CommandLineRunner commandLineRunner;

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private UsuarioRepository usuarioRepository;

    private Condominium condominioTeste;

    @BeforeEach
    void setUp() {
        condominioTeste = new Condominium();
        condominioTeste.setNome("Condo Teste DataJPA");
        condominioTeste.setEndereco("Rua Teste JPA, 456");
        entityManager.persistAndFlush(condominioTeste);
    }

    @Test
    void quandoFindByEmail_ComEmailExistente_DeveRetornarUsuario() {
        Morador morador = new Morador();
        morador.setNome("JPA Teste Email");
        morador.setEmail("jpa.teste.email@condo.com");
        morador.setSenha("123");
        morador.setUnidade("JT01");
        morador.setCondominio(condominioTeste);
        Set<Role> roles = new HashSet<>();
        roles.add(Role.MORADOR);
        morador.setRoles(roles);
        entityManager.persistAndFlush(morador);

        Optional<Usuario> encontrado = usuarioRepository.findByEmail("jpa.teste.email@condo.com");

        assertThat(encontrado).isPresent();
        assertThat(encontrado.get().getNome()).isEqualTo("JPA Teste Email");
    }

    @Test
    void quandoFindByEmail_ComEmailInexistente_DeveRetornarVazio() {
        Optional<Usuario> encontrado = usuarioRepository.findByEmail("inexistente.jpa@condo.com");
        assertThat(encontrado).isNotPresent();
    }

    @Test
    void quandoSalvarNovoMorador_DevePersistirCorretamente() {
        Morador novoMorador = new Morador();
        novoMorador.setNome("Maria Nova Moradora JPA");
        novoMorador.setEmail("maria.nova.jpa@condo.com");
        novoMorador.setSenha("outrasenha456");
        novoMorador.setUnidade("Casa 2B JPA");
        novoMorador.setCpf("000.111.222-33");
        novoMorador.setTelefone("2288776655");
        novoMorador.setCondominio(condominioTeste);
        Set<Role> roles = new HashSet<>();
        roles.add(Role.MORADOR);
        novoMorador.setRoles(roles);

        Usuario usuarioSalvo = usuarioRepository.save(novoMorador);

        assertThat(usuarioSalvo).isNotNull();
        assertThat(usuarioSalvo.getId()).isNotNull().isPositive();

        Usuario usuarioDoBanco = entityManager.find(Usuario.class, usuarioSalvo.getId());
        assertThat(usuarioDoBanco).isNotNull();
        assertThat(usuarioDoBanco.getEmail()).isEqualTo("maria.nova.jpa@condo.com");
    }
}