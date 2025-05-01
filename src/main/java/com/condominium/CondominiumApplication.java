package com.condominium;

import com.condominium.model.*;
import com.condominium.service.CondominiumService;
import com.condominium.service.ManutencaoService;
import com.condominium.service.ReservaService;
import com.condominium.service.AssembleiaService;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;

public class CondominiumApplication {

    private static CondominiumService condominiumService;
    private static ReservaService reservaService;
    private static ManutencaoService manutencaoService = new ManutencaoService();
    private static AssembleiaService assembleiaService = new AssembleiaService();

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
                opcao = 0;
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
                case 9 -> agendarAssembleia();
                case 10 -> visualizarAssembleias();
                case 11 -> atualizarAssembleia();
                case 12 -> System.out.println("Saindo...");
                default -> System.out.println("Opção inválida, tente novamente.");
            }

            if (opcao != 12) {
                System.out.println("\nPressione ENTER para voltar ao menu...");
                scanner.nextLine();
                limparTela();
            }
        } while (opcao != 12);

        scanner.close();
    }

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
        System.out.println("9 - Agendar Assembleia (Síndico)");
        System.out.println("10 - Visualizar Assembleias (Morador)");
        System.out.println("11 - Atualizar Assembleia (Síndico)");
        System.out.println("12 - Sair");
        System.out.println("Escolha uma opção: ");
    }

    private static void limparTela() {
        for (int i = 0; i < 30; i++) System.out.println();
    }

    // Cadastro
    private static void adicionarMorador() {
        System.out.println("--- Cadastro de Morador ---");
        System.out.println("Nome: ");
        String nome = scanner.nextLine();
        System.out.println("CPF: ");
        String cpf = scanner.nextLine();
        System.out.println("Email: ");
        String email = scanner.nextLine();
        System.out.println("Telefone: ");
        String telefone = scanner.nextLine();
        System.out.println("Senha: ");
        String senha = scanner.nextLine();
        System.out.println("Unidade: ");
        String unidade = scanner.nextLine();

        Morador morador = condominiumService.adicionarMorador(nome, cpf, email, telefone, senha, unidade);
        System.out.println("Morador cadastrado com sucesso: " + morador);
    }

    private static void adicionarSindico() {
        System.out.println("--- Cadastro de Síndico ---");
        System.out.println("Nome: ");
        String nome = scanner.nextLine();
        System.out.println("CPF: ");
        String cpf = scanner.nextLine();
        System.out.println("Email: ");
        String email = scanner.nextLine();
        System.out.println("Telefone: ");
        String telefone = scanner.nextLine();
        System.out.println("Senha: ");
        String senha = scanner.nextLine();
        System.out.println("Bloco Responsável: ");
        String bloco = scanner.nextLine();

        Sindico sindico = condominiumService.adicionarSindico(nome, cpf, email, telefone, senha, bloco);
        System.out.println("Síndico cadastrado com sucesso: " + sindico);
    }

    private static void adicionarAdministrador() {
        System.out.println("--- Cadastro de Administrador ---");
        System.out.println("Nome: ");
        String nome = scanner.nextLine();
        System.out.println("CPF: ");
        String cpf = scanner.nextLine();
        System.out.println("Email: ");
        String email = scanner.nextLine();
        System.out.println("Telefone: ");
        String telefone = scanner.nextLine();
        System.out.println("Senha: ");
        String senha = scanner.nextLine();
        System.out.println("Setor: ");
        String setor = scanner.nextLine();

        Administrador admin = condominiumService.adicionarAdministrador(nome, cpf, email, telefone, senha, setor);
        System.out.println("Administrador cadastrado com sucesso: " + admin);
    }

    // Reserva
    private static void reservarAreaComum() {
        System.out.println("--- Reservar Área Comum ---");
        System.out.println("1 - " + salaFesta);
        System.out.println("2 - " + churrasqueira);
        System.out.println("Escolha a área (1 ou 2): ");
        int opcaoArea = Integer.parseInt(scanner.nextLine());
        AreaComum areaSelecionada = (opcaoArea == 1) ? salaFesta : churrasqueira;

        System.out.println("Data da reserva (AAAA-MM-DD): ");
        LocalDate data = LocalDate.parse(scanner.nextLine());
        System.out.println("Hora de Início (HH:MM): ");
        LocalTime horaInicio = LocalTime.parse(scanner.nextLine());
        System.out.println("Hora de Fim (HH:MM): ");
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

    // Manutenção
    private static void reportarProblema() {
        System.out.println("--- Reportar Problema de Manutenção ---");
        System.out.println("Local: ");
        String local = scanner.nextLine();
        System.out.println("Descrição do problema: ");
        String descricao = scanner.nextLine();
        System.out.println("Envolve agentes externos? (s/n): ");
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
        System.out.println("Escolha: ");
        int escolha = Integer.parseInt(scanner.nextLine());

        switch (escolha) {
            case 1 -> {
                System.out.println("Comentário: ");
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

    // Assembleia
    private static void agendarAssembleia() {
        System.out.println("--- Agendar Assembleia ---");
        System.out.println("Data (AAAA-MM-DD): ");
        LocalDate data = LocalDate.parse(scanner.nextLine());
        System.out.println("Horário (HH:MM): ");
        LocalTime horario = LocalTime.parse(scanner.nextLine());
        System.out.println("Local: ");
        String local = scanner.nextLine();
        System.out.println("Pauta: ");
        String pauta = scanner.nextLine();
        System.out.println("Participantes esperados (numero de participantes): ");
        int participantes = Integer.parseInt(scanner.nextLine());

        if (local.isBlank() || pauta.isBlank()) {
            System.out.println("Local e pauta são obrigatórios!");
            return;
        }

        Assembleia a = assembleiaService.agendarAssembleia(data, horario, local, pauta, participantes);
        System.out.println("Assembleia agendada: " + a);
    }

    private static void visualizarAssembleias() {
        System.out.println("--- Assembleias Agendadas ---");
        List<Assembleia> lista = assembleiaService.listarAssembleias();
        if (lista.isEmpty()) {
            System.out.println("Nenhuma assembleia agendada.");
        } else {
            lista.forEach(System.out::println);
        }
    }

    private static void atualizarAssembleia() {
        System.out.println("--- Atualizar Assembleia ---");
        visualizarAssembleias();
        System.out.println("ID da assembleia a atualizar: ");
        Long id = Long.parseLong(scanner.nextLine());

        Optional<Assembleia> opt = assembleiaService.buscarPorId(id);
        if (opt.isEmpty()) {
            System.out.println("Assembleia não encontrada.");
            return;
        }

        System.out.println("Nova Data (AAAA-MM-DD): ");
        LocalDate novaData = LocalDate.parse(scanner.nextLine());
        System.out.println("Novo Horário (HH:MM): ");
        LocalTime novoHorario = LocalTime.parse(scanner.nextLine());
        System.out.println("Nova Pauta: ");
        String novaPauta = scanner.nextLine();

        if (novaPauta.isBlank()) {
            System.out.println("Pauta é obrigatória!");
            return;
        }

        boolean sucesso = assembleiaService.atualizarAssembleia(id, novaData, novoHorario, novaPauta);
        System.out.println(sucesso ? "Assembleia atualizada!" : "Falha na atualização.");
    }
}
