package com.condominium;

import com.condominium.model.*;
import com.condominium.service.CondominiumService;
import com.condominium.service.ManutencaoService;
import com.condominium.service.ReservaService;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;

public class CondominiumApplication {

    private static CondominiumService condominiumService;
    private static ReservaService reservaService;
    private static ManutencaoService manutencaoService = new ManutencaoService();

    private static AreaComum salaFesta;
    private static AreaComum churrasqueira;
    private static Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        System.out.print("Digite o nome do Condomínio: ");
        String nomeCondominium = scanner.nextLine();

        condominiumService = new CondominiumService(nomeCondominium);
        reservaService = new ReservaService();

        // Criando áreas comuns
        salaFesta = new AreaComum(1L, "Sala de Festa", "Espaço para festas e eventos");
        churrasqueira = new AreaComum(2L, "Churrasqueira", "Área com churrasqueira e espaço gourmet");

        int opcao;
        do {
            exibirMenu();
            try {
                opcao = Integer.parseInt(scanner.nextLine());
            } catch (NumberFormatException e) {
                opcao = 0; // se erro, volta ao menu
            }

            limparTela();
            switch (opcao) {
                case 1 -> adicionarMorador();
                case 2 -> adicionarSindico();
                case 3 -> adicionarAdministrador();
                case 4 -> reservarAreaComum();
                case 5 -> listarReservas();
                case 6 -> exibirDadosCondominio();
                case 7 -> reportarProblema();
                case 8 -> gerenciarManutencoes();
                case 9 -> System.out.println("Saindo...");
                default -> System.out.println("Opção inválida, tente novamente.");
            }

            if (opcao != 9) {
                System.out.println("\nPressione ENTER para voltar ao menu...");
                scanner.nextLine();
                limparTela();
            }
        } while (opcao != 9);

        scanner.close();
    }

    // Exibe o menu principal
    private static void exibirMenu() {
        System.out.println("--- MENU ---");
        System.out.println("1 - Adicionar Morador");
        System.out.println("2 - Adicionar Síndico");
        System.out.println("3 - Adicionar Administrador");
        System.out.println("4 - Reservar Área Comum");
        System.out.println("5 - Listar Reservas");
        System.out.println("6 - Exibir dados do Condomínio");
        System.out.println("7 - Reportar Problema de Manutenção");
        System.out.println("8 - Gerenciar Manutenções (Administrador)");
        System.out.println("9 - Sair");
        System.out.print("Escolha uma opção: ");
    }

    private static void limparTela() {
        for (int i = 0; i < 30; i++) System.out.println();
    }

    // cadastro

    private static void adicionarMorador() {
        System.out.println("--- Cadastro de Morador ---");
        System.out.print("Nome: ");
        String nome = scanner.nextLine();
        System.out.print("CPF: ");
        String cpf = scanner.nextLine();
        System.out.print("Email: ");
        String email = scanner.nextLine();
        System.out.print("Telefone: ");
        String telefone = scanner.nextLine();
        System.out.print("Senha: ");
        String senha = scanner.nextLine();
        System.out.print("Unidade: ");
        String unidade = scanner.nextLine();

        Morador morador = condominiumService.adicionarMorador(nome, cpf, email, telefone, senha, unidade);
        System.out.println("Morador cadastrado com sucesso: " + morador);
    }

    private static void adicionarSindico() {
        System.out.println("--- Cadastro de Síndico ---");
        System.out.print("Nome: ");
        String nome = scanner.nextLine();
        System.out.print("CPF: ");
        String cpf = scanner.nextLine();
        System.out.print("Email: ");
        String email = scanner.nextLine();
        System.out.print("Telefone: ");
        String telefone = scanner.nextLine();
        System.out.print("Senha: ");
        String senha = scanner.nextLine();
        System.out.print("Bloco Responsável: ");
        String bloco = scanner.nextLine();

        Sindico sindico = condominiumService.adicionarSindico(nome, cpf, email, telefone, senha, bloco);
        System.out.println("Síndico cadastrado com sucesso: " + sindico);
    }

    private static void adicionarAdministrador() {
        System.out.println("--- Cadastro de Administrador ---");
        System.out.print("Nome: ");
        String nome = scanner.nextLine();
        System.out.print("CPF: ");
        String cpf = scanner.nextLine();
        System.out.print("Email: ");
        String email = scanner.nextLine();
        System.out.print("Telefone: ");
        String telefone = scanner.nextLine();
        System.out.print("Senha: ");
        String senha = scanner.nextLine();
        System.out.print("Setor: ");
        String setor = scanner.nextLine();

        Administrador admin = condominiumService.adicionarAdministrador(nome, cpf, email, telefone, senha, setor);
        System.out.println("Administrador cadastrado com sucesso: " + admin);
    }

    // reserva

    private static void reservarAreaComum() {
        System.out.println("--- Reservar Área Comum ---");
        System.out.println("1 - " + salaFesta);
        System.out.println("2 - " + churrasqueira);
        System.out.print("Escolha a área (1 ou 2): ");
        int opcaoArea = Integer.parseInt(scanner.nextLine());
        AreaComum areaSelecionada = (opcaoArea == 1) ? salaFesta : churrasqueira;

        System.out.print("Data da reserva (AAAA-MM-DD): ");
        LocalDate data = LocalDate.parse(scanner.nextLine());
        System.out.print("Hora de Início (HH:MM): ");
        LocalTime horaInicio = LocalTime.parse(scanner.nextLine());
        System.out.print("Hora de Fim (HH:MM): ");
        LocalTime horaFim = LocalTime.parse(scanner.nextLine());

        Reserva reserva = reservaService.reservarArea(areaSelecionada, data, horaInicio, horaFim);
        if (reserva != null) {
            System.out.println("Reserva criada com sucesso: " + reserva);
        } else {
            System.out.println("Horário indisponível para reserva!");
        }
    }

    private static void listarReservas() {
        System.out.println("--- Reservas Registradas ---");
        reservaService.listarReservas().forEach(System.out::println);
    }

    private static void exibirDadosCondominio() {
        System.out.println("--- Dados do Condomínio ---");
        System.out.println(condominiumService.getCondominium());
    }

    // manutenção

    private static void reportarProblema() {
        System.out.println("--- Reportar Problema de Manutenção ---");
        System.out.print("Local: ");
        String local = scanner.nextLine();
        System.out.print("Descrição do problema: ");
        String descricao = scanner.nextLine();
        System.out.print("Envolve agentes externos? (s/n): ");
        boolean externo = scanner.nextLine().equalsIgnoreCase("s");

        ReportManutencao report = manutencaoService.reportarProblema(local, descricao, externo);
        System.out.println("Problema registrado! Protocolo: " + report.getProtocolo());
    }

    private static void gerenciarManutencoes() {
        System.out.println("--- Gerenciar Relatórios de Manutenção ---");
        List<ReportManutencao> lista = manutencaoService.listarTodos();
        if (lista.isEmpty()) {
            System.out.println("Nenhum problema registrado.");
            return;
        }

        lista.forEach(System.out::println);

        System.out.print("ID do problema para gerenciar (ou 0 para voltar): ");
        Long id = Long.parseLong(scanner.nextLine());
        if (id == 0) return;

        Optional<ReportManutencao> opt = manutencaoService.findById(id);
        if (opt.isEmpty()) {
            System.out.println("Relatório não encontrado.");
            return;
        }
        ReportManutencao report = opt.get();

        System.out.println("Detalhes do relatório:\n" + report);
        System.out.println("Histórico:");
        report.getHistorico().forEach(System.out::println);

        System.out.println("\n1 - Comentar\n2 - Alterar Status\n3 - Voltar");
        System.out.print("Escolha: ");
        int escolha = Integer.parseInt(scanner.nextLine());

        switch (escolha) {
            case 1 -> {
                System.out.print("Comentário: ");
                String comentario = scanner.nextLine();
                manutencaoService.adicionarComentario(id, comentario);
                System.out.println("Comentário adicionado!");
            }
            case 2 -> {
                System.out.println("Status disponíveis: " + List.of(StatusManutencao.values()));
                System.out.print("Novo Status: ");
                StatusManutencao novoStatus = StatusManutencao.valueOf(scanner.nextLine().trim().toUpperCase());
                manutencaoService.alterarStatus(id, novoStatus);
                System.out.println("Status atualizado!");
            }
            case 3 -> System.out.println("Voltando...");
            default -> System.out.println("Opção inválida.");
        }
    }
}
