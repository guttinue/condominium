package com.condominium;

import com.condominium.model.*;
import com.condominium.service.CondominiumService;
import com.condominium.service.ReservaService;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Scanner;

public class CondominiumApplication {

    private static CondominiumService condominiumService;
    private static ReservaService reservaService;
    private static AreaComum salaFesta;
    private static AreaComum churrasqueira;
    private static Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        System.out.print("Digite o nome do Condomínio: ");
        String nomeCondominium = scanner.nextLine();
        condominiumService = new CondominiumService(nomeCondominium);
        reservaService = new ReservaService();

        salaFesta = new AreaComum(1L, "Sala de Festa", "Espaço para festas e eventos");
        churrasqueira = new AreaComum(2L, "Churrasqueira", "Área com churrasqueira e espaço gourmet");

        int opcao = 0;
        while (opcao != 7) {
            exibirMenu();
            try {
                opcao = Integer.parseInt(scanner.nextLine());
            } catch(NumberFormatException e){
                opcao = 0;
            }

            switch (opcao) {
                case 1:
                    limparTela();
                    adicionarMorador();
                    break;
                case 2:
                    limparTela();
                    adicionarSindico();
                    break;
                case 3:
                    limparTela();
                    adicionarAdministrador();
                    break;
                case 4:
                    limparTela();
                    reservarAreaComum();
                    break;
                case 5:
                    limparTela();
                    listarReservas();
                    break;
                case 6:
                    limparTela();
                    exibirDadosCondominio();
                    break;
                case 7:
                    System.out.println("Saindo...");
                    break;
                default:
                    System.out.println("Opção inválida, tente novamente.");
                    break;
            }
            if(opcao != 7) {
                System.out.println("\nPressione ENTER para voltar ao menu...");
                scanner.nextLine();
                limparTela();
            }
        }
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
        System.out.println("7 - Sair");
        System.out.print("Escolha uma opção: ");
    }

    private static void limparTela() {
        for (int i = 0; i < 40; ++i)
            System.out.println();
    }

    private static void adicionarMorador() {
        System.out.println("--- Cadastro de Morador ---");
        System.out.print("Nome do Morador: ");
        String nomeMorador = scanner.nextLine();
        System.out.print("CPF: ");
        String cpfMorador = scanner.nextLine();
        System.out.print("Email: ");
        String emailMorador = scanner.nextLine();
        System.out.print("Telefone: ");
        String telefoneMorador = scanner.nextLine();
        System.out.print("Senha: ");
        String senhaMorador = scanner.nextLine();
        System.out.print("Unidade: ");
        String unidade = scanner.nextLine();

        Morador morador = condominiumService.adicionarMorador(nomeMorador, cpfMorador, emailMorador, telefoneMorador, senhaMorador, unidade);
        System.out.println("Morador adicionado: " + morador);
    }

    private static void adicionarSindico() {
        System.out.println("--- Cadastro de Síndico ---");
        System.out.print("Nome do Síndico: ");
        String nomeSindico = scanner.nextLine();
        System.out.print("CPF: ");
        String cpfSindico = scanner.nextLine();
        System.out.print("Email: ");
        String emailSindico = scanner.nextLine();
        System.out.print("Telefone: ");
        String telefoneSindico = scanner.nextLine();
        System.out.print("Senha: ");
        String senhaSindico = scanner.nextLine();
        System.out.print("Bloco responsável: ");
        String bloco = scanner.nextLine();

        Sindico sindico = condominiumService.adicionarSindico(nomeSindico, cpfSindico, emailSindico, telefoneSindico, senhaSindico, bloco);
        System.out.println("Síndico adicionado: " + sindico);
    }

    private static void adicionarAdministrador() {
        System.out.println("--- Cadastro de Administrador ---");
        System.out.print("Nome do Administrador: ");
        String nomeAdmin = scanner.nextLine();
        System.out.print("CPF: ");
        String cpfAdmin = scanner.nextLine();
        System.out.print("Email: ");
        String emailAdmin = scanner.nextLine();
        System.out.print("Telefone: ");
        String telefoneAdmin = scanner.nextLine();
        System.out.print("Senha: ");
        String senhaAdmin = scanner.nextLine();
        System.out.print("Setor: ");
        String setor = scanner.nextLine();

        Administrador admin = condominiumService.adicionarAdministrador(nomeAdmin, cpfAdmin, emailAdmin, telefoneAdmin, senhaAdmin, setor);
        System.out.println("Administrador adicionado: " + admin);
    }

    private static void reservarAreaComum() {
        System.out.println("--- Reservar Área Comum ---");
        System.out.println("Áreas disponíveis:");
        System.out.println("1 - " + salaFesta);
        System.out.println("2 - " + churrasqueira);
        System.out.print("Escolha a área (1 ou 2): ");
        int opcaoArea = Integer.parseInt(scanner.nextLine());
        AreaComum areaSelecionada = (opcaoArea == 1) ? salaFesta : churrasqueira;

        System.out.print("Data da reserva (YYYY-MM-DD): ");
        LocalDate data = LocalDate.parse(scanner.nextLine());
        System.out.print("Hora de início (HH:MM): ");
        LocalTime horaInicio = LocalTime.parse(scanner.nextLine());
        System.out.print("Hora de término (HH:MM): ");
        LocalTime horaFim = LocalTime.parse(scanner.nextLine());

        Reserva reserva = reservaService.reservarArea(areaSelecionada, data, horaInicio, horaFim);
        if (reserva != null) {
            System.out.println("Reserva criada com sucesso (status: " + reserva.getStatus() + "): " + reserva);
        } else {
            System.out.println("Horário indisponível para reserva!");
        }
    }

    private static void listarReservas() {
        System.out.println("--- Lista de Reservas ---");
        reservaService.listarReservas().forEach(System.out::println);
    }

    private static void exibirDadosCondominio() {
        System.out.println("--- Dados do Condomínio ---");
        System.out.println(condominiumService.getCondominium());
    }
}
