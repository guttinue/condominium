package com.condominium.app.sindico;

import com.condominium.model.Assembleia;
import com.condominium.service.AssembleiaService;
import com.condominium.service.CondominiumService;
import com.condominium.service.ManutencaoService;
import com.condominium.service.ReservaService;
import com.condominium.util.InputValidator;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;

public class SindicoApp {
    private static final Scanner scanner = new Scanner(System.in);

    public static void run(
            CondominiumService condoService,
            ReservaService reservaService,
            ManutencaoService manutService,
            AssembleiaService assembleiaService
    ) {
        int op;
        do {
            System.out.println("\n--- MODO SÍNDICO ---");
            System.out.println("1 - Agendar Assembleia");
            System.out.println("2 - Visualizar Assembleias");
            System.out.println("3 - Atualizar Assembleia");
            System.out.println("0 - Logout");
            System.out.print("Opção: ");
            try {
                op = Integer.parseInt(scanner.nextLine());
            } catch (NumberFormatException e) {
                op = -1;
            }

            switch (op) {
                case 1 -> agendarAssembleia(assembleiaService);
                case 2 -> visualizarAssembleias(assembleiaService);
                case 3 -> atualizarAssembleia(assembleiaService);
                case 0 -> System.out.println("Logout síndico.");
                default -> System.out.println("Opção inválida.");
            }

            if (op != 0) {
                System.out.println("\nPressione ENTER para voltar ao menu...");
                scanner.nextLine();
            }

        } while (op != 0);
    }

    private static void agendarAssembleia(AssembleiaService service) {
        System.out.println("--- Agendar Assembleia (ENTER em branco cancela) ---");
        System.out.print("Data (YYYY-MM-DD): ");
        String sData = scanner.nextLine();
        if (sData.isBlank()) return;
        LocalDate data;
        try {
            data = InputValidator.parseDate(sData);
        } catch (IllegalArgumentException e) {
            System.out.println(e.getMessage());
            return;
        }
        System.out.print("Horário (HH:MM): ");
        String sHora = scanner.nextLine();
        if (sHora.isBlank()) return;
        LocalTime hora;
        try {
            hora = InputValidator.parseTime(sHora);
        } catch (IllegalArgumentException e) {
            System.out.println(e.getMessage());
            return;
        }
        System.out.print("Local: ");
        String local = scanner.nextLine();
        if (local.isBlank()) return;
        System.out.print("Pauta: ");
        String pauta = scanner.nextLine();
        if (pauta.isBlank()) return;
        System.out.print("Participantes esperados: ");
        String sPart = scanner.nextLine();
        if (sPart.isBlank()) return;
        int participantes;
        try {
            participantes = Integer.parseInt(sPart);
        } catch (NumberFormatException e) {
            System.out.println("Número de participantes inválido.");
            return;
        }

        Assembleia a = service.agendarAssembleia(data, hora, local, pauta, participantes);
        System.out.println("Assembleia agendada: " + a);
    }

    private static void visualizarAssembleias(AssembleiaService service) {
        System.out.println("--- Assembleias Agendadas ---");
        List<Assembleia> lista = service.listarAssembleias();
        if (lista.isEmpty()) {
            System.out.println("Nenhuma assembleia agendada.");
        } else {
            lista.forEach(System.out::println);
        }
    }

    private static void atualizarAssembleia(AssembleiaService service) {
        System.out.println("--- Atualizar Assembleia (ENTER em branco cancela) ---");
        service.listarAssembleias().forEach(System.out::println);
        System.out.print("ID da assembleia: ");
        String sId = scanner.nextLine();
        if (sId.isBlank()) return;
        Long id;
        try {
            id = Long.parseLong(sId);
        } catch (NumberFormatException e) {
            System.out.println("ID inválido.");
            return;
        }
        Optional<Assembleia> opt = service.buscarPorId(id);
        if (opt.isEmpty()) {
            System.out.println("Assembleia não encontrada.");
            return;
        }
        System.out.print("Nova Data (YYYY-MM-DD): ");
        String sData = scanner.nextLine();
        if (sData.isBlank()) return;
        LocalDate nd;
        try {
            nd = InputValidator.parseDate(sData);
        } catch (IllegalArgumentException e) {
            System.out.println(e.getMessage());
            return;
        }
        System.out.print("Novo Horário (HH:MM): ");
        String sHora = scanner.nextLine();
        if (sHora.isBlank()) return;
        LocalTime nh;
        try {
            nh = InputValidator.parseTime(sHora);
        } catch (IllegalArgumentException e) {
            System.out.println(e.getMessage());
            return;
        }
        System.out.print("Nova Pauta: ");
        String np = scanner.nextLine();
        if (np.isBlank()) return;

        boolean ok = service.atualizarAssembleia(id, nd, nh, np);
        System.out.println(ok ? "Atualizada!" : "Falha na atualização.");
    }
}
