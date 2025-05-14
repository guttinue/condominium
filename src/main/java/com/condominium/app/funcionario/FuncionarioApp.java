package com.condominium.app.funcionario;

import com.condominium.model.Morador;
import com.condominium.model.Sindico;
import com.condominium.model.Administrador;
import com.condominium.service.CondominiumService;
import com.condominium.service.ReservaService;
import com.condominium.service.ManutencaoService;
import com.condominium.service.AssembleiaService;
import com.condominium.util.InputValidator;

import java.util.Scanner;

public class FuncionarioApp {
    private static final Scanner scanner = new Scanner(System.in);

    public static void run(
            CondominiumService condominiumService,
            ReservaService reservaService,
            ManutencaoService manutencaoService,
            AssembleiaService assembleiaService
    ) {
        int opcao;
        do {
            System.out.println("\n--- MODO FUNCIONÁRIO ---");
            System.out.println("1 - Adicionar Morador");
            System.out.println("2 - Adicionar Síndico");
            System.out.println("3 - Adicionar Administrador");
            System.out.println("0 - Logout");
            System.out.print("Opção: ");
            try { opcao = Integer.parseInt(scanner.nextLine()); }
            catch (NumberFormatException e) { opcao = -1; }

            switch (opcao) {
                case 1 -> adicionarMorador(condominiumService);
                case 2 -> adicionarSindico(condominiumService);
                case 3 -> adicionarAdministrador(condominiumService);
                case 0 -> System.out.println("Logout funcionário.");
                default -> System.out.println("Opção inválida.");
            }

            if (opcao != 0) {
                System.out.println("\nPressione ENTER para voltar ao menu...");
                scanner.nextLine();
            }
        } while (opcao != 0);
    }

    private static void adicionarMorador(CondominiumService svc) {
        System.out.println("--- Cadastro de Morador (ENTER cancela) ---");
        System.out.print("Nome: ");
        String nome = scanner.nextLine(); if (nome.isBlank()) return;
        System.out.print("CPF: ");
        String cpf = scanner.nextLine(); if (cpf.isBlank()) return;
        System.out.print("Email: ");
        String email = scanner.nextLine(); if (email.isBlank()) return;
        System.out.print("Telefone: ");
        String telefone = scanner.nextLine(); if (telefone.isBlank()) return;
        System.out.print("Senha: ");
        String senha = scanner.nextLine(); if (senha.isBlank()) return;
        System.out.print("Unidade: ");
        String unidade = scanner.nextLine(); if (unidade.isBlank()) return;

        Morador m = svc.adicionarMorador(
                nome, cpf, email, telefone, senha, unidade
        );
        System.out.println("Morador cadastrado: " + m);
    }

    private static void adicionarSindico(CondominiumService svc) {
        System.out.println("--- Cadastro de Síndico (ENTER cancela) ---");
        System.out.print("Nome: ");
        String nome = scanner.nextLine(); if (nome.isBlank()) return;
        System.out.print("CPF: ");
        String cpf = scanner.nextLine(); if (cpf.isBlank()) return;
        System.out.print("Email: ");
        String email = scanner.nextLine(); if (email.isBlank()) return;
        System.out.print("Telefone: ");
        String telefone = scanner.nextLine(); if (telefone.isBlank()) return;
        System.out.print("Senha: ");
        String senha = scanner.nextLine(); if (senha.isBlank()) return;
        System.out.print("Bloco Responsável: ");
        String bloco = scanner.nextLine(); if (bloco.isBlank()) return;

        Sindico s = svc.adicionarSindico(
                nome, cpf, email, telefone, senha, bloco
        );
        System.out.println("Síndico cadastrado: " + s);
    }

    private static void adicionarAdministrador(CondominiumService svc) {
        System.out.println("--- Cadastro de Administrador (ENTER cancela) ---");
        System.out.print("Nome: ");
        String nome = scanner.nextLine(); if (nome.isBlank()) return;
        System.out.print("CPF: ");
        String cpf = scanner.nextLine(); if (cpf.isBlank()) return;
        System.out.print("Email: ");
        String email = scanner.nextLine(); if (email.isBlank()) return;
        System.out.print("Telefone: ");
        String telefone = scanner.nextLine(); if (telefone.isBlank()) return;
        System.out.print("Senha: ");
        String senha = scanner.nextLine(); if (senha.isBlank()) return;
        System.out.print("Setor: ");
        String setor = scanner.nextLine(); if (setor.isBlank()) return;

        Administrador a = svc.adicionarAdministrador(
                nome, cpf, email, telefone, senha, setor
        );
        System.out.println("Administrador cadastrado: " + a);
    }
}
