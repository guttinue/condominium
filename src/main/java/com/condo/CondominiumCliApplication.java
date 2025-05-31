package com.condo;

import com.condo.domain.Morador;
import com.condo.domain.Role;
import com.condo.domain.Sindico;
import com.condo.domain.Usuario;
import com.condo.domain.Administrador;
import com.condo.menu.FuncionarioMenu;
import com.condo.menu.MoradorMenu;
import com.condo.menu.SindicoMenu;
import com.condo.service.LoginService; // Apenas um import é necessário
// Outros services serão injetados conforme necessário
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.dao.DataIntegrityViolationException;

// import java.util.InputMismatchException; // Não usado diretamente aqui, mas pode estar em menus
import java.util.Optional;
import java.util.Scanner;
import java.util.Set;

@SpringBootApplication
public class CondominiumCliApplication {

    private static final Logger log = LoggerFactory.getLogger(CondominiumCliApplication.class);

    public static void main(String[] args) {
        SpringApplication.run(CondominiumCliApplication.class, args);
    }

    @Bean
    public CommandLineRunner cli(
            LoginService loginService,
            MoradorMenu moradorMenu,
            SindicoMenu sindicoMenu,
            FuncionarioMenu funcionarioMenu
    ) {
        return args -> {
            Scanner sc = new Scanner(System.in);
            boolean sairDoSistema = false;

            System.out.println("========================================");
            System.out.println(" BEM-VINDO AO SISTEMA DE GESTÃO CONDOMINIAL ");
            System.out.println("========================================");

            while (!sairDoSistema) {
                Usuario usuarioLogado = null;


                while (usuarioLogado == null && !sairDoSistema) {
                    try {
                        System.out.print("\nEmail (ou digite 'sair' para fechar o sistema): ");
                        String email = sc.nextLine().trim();

                        if ("sair".equalsIgnoreCase(email)) {
                            sairDoSistema = true;
                            break;
                        }

                        System.out.print("Senha: ");
                        String senha = sc.nextLine().trim();

                        if (email.isEmpty() || senha.isEmpty()) {
                            System.out.println("Email e senha não podem ser vazios. Tente novamente.");
                            continue;
                        }

                        Optional<Usuario> usuarioOpt = loginService.autenticar(email, senha);
                        usuarioLogado = usuarioOpt.orElse(null);

                        if (usuarioLogado == null && !sairDoSistema) {
                            System.out.println("Credenciais inválidas. Tente novamente.");
                        }
                    } catch (DataIntegrityViolationException e) {
                        log.error("Erro de integridade de dados durante o login: {}", e.getMessage());
                        System.out.println("Ocorreu um erro no banco de dados. Por favor, contate o suporte.");
                    } catch (Exception e) {
                        log.error("Erro inesperado durante o login: {}", e.getMessage(), e);
                        System.out.println("Ocorreu um erro inesperado. Tente novamente.");
                    }
                }

                if (sairDoSistema) {
                    break;
                }

                if (usuarioLogado != null) {
                    System.out.println("\n----------------------------------------");
                    System.out.println("Login bem-sucedido!");
                    System.out.println("Bem-vindo(a), " + usuarioLogado.getNome() + "!");
                    System.out.println("Seus papéis: " + usuarioLogado.getRoles());
                    System.out.println("----------------------------------------\n");

                    Set<Role> roles = usuarioLogado.getRoles();

                    if (roles.contains(Role.SINDICO) && usuarioLogado instanceof Sindico) {
                        sindicoMenu.exibirMenu(sc, (Sindico) usuarioLogado);
                    } else if (roles.contains(Role.FUNCIONARIO) && usuarioLogado instanceof Administrador) {
                        funcionarioMenu.exibirMenu(sc, (Administrador) usuarioLogado);
                    } else if (roles.contains(Role.MORADOR) && usuarioLogado instanceof Morador) {
                        moradorMenu.exibirMenu(sc, (Morador) usuarioLogado);
                    } else {
                        System.out.println(
                                "Você não tem um papel com menu principal definido ou o tipo de usuário não corresponde.");
                        log.warn("Usuário {} com papéis {} não correspondeu a nenhum menu principal específico.",
                                usuarioLogado.getEmail(), roles);
                    }

                    System.out.println("\nLogout efetuado. Retornando à tela de login...");
                }
            }

            System.out.println("\nSaindo do sistema de gestão condominial...");
        };
    }
}