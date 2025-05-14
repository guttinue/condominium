package com.condominium.app.morador;

import com.condominium.model.AreaComum;
import com.condominium.model.Reserva;
import com.condominium.model.ReportManutencao;
import com.condominium.model.Assembleia;
import com.condominium.service.CondominiumService;
import com.condominium.service.AreaComumService;
import com.condominium.service.ReservaService;
import com.condominium.service.ManutencaoService;
import com.condominium.service.AssembleiaService;
import com.condominium.util.InputValidator;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Scanner;

public class MoradorApp {
    private static final Scanner scanner = new Scanner(System.in);

    public static void run(
            CondominiumService condoSvc,
            AreaComumService areaSvc,
            ReservaService reservaSvc,
            ManutencaoService manutSvc,
            AssembleiaService assembleiaSvc
    ) {
        int op;
        do {
            System.out.println("\n--- MODO MORADOR ---");
            System.out.println("1 - Reservar Área Comum");
            System.out.println("2 - Listar Reservas");
            System.out.println("3 - Reportar Problema");
            System.out.println("4 - Visualizar Assembleias");
            System.out.println("0 - Logout");
            System.out.print("Opção: ");
            try { op = Integer.parseInt(scanner.nextLine()); }
            catch (NumberFormatException e) { op = -1; }

            switch (op) {
                case 1 -> reservarArea(condoSvc, areaSvc, reservaSvc);
                case 2 -> listarReservas(reservaSvc);
                case 3 -> reportarProblema(manutSvc);
                case 4 -> visualizarAssembleias(assembleiaSvc);
                case 0 -> System.out.println("Logout morador.");
                default -> System.out.println("Opção inválida.");
            }
            if (op != 0) {
                System.out.println("\nPressione ENTER para voltar ao menu...");
                scanner.nextLine();
            }
        } while (op != 0);
    }

    private static void reservarArea(
            CondominiumService condoSvc,
            AreaComumService areaSvc,
            ReservaService reservaSvc
    ) {
        System.out.println("--- Reservar Área Comum (ENTER cancela) ---");
        // listar
        areaSvc.listarAreas().forEach(a -> System.out.println(a.getId() + " - " + a.getNome()));
        System.out.print("Escolha o ID da área: ");
        String sid = scanner.nextLine();
        if (sid.isBlank()) return;
        long id;
        try { id = Long.parseLong(sid); }
        catch (NumberFormatException e) { System.out.println("ID inválido."); return; }
        AreaComum area = areaSvc.buscarPorId(id).orElse(null);
        if (area == null) { System.out.println("Área não encontrada."); return; }

        System.out.print("Data (YYYY-MM-DD): ");
        String sData = scanner.nextLine(); if (sData.isBlank()) return;
        LocalDate data;
        try { data = InputValidator.parseDate(sData); }
        catch (IllegalArgumentException e) { System.out.println(e.getMessage()); return; }

        System.out.print("Hora Início (HH:MM): ");
        String sHi = scanner.nextLine(); if (sHi.isBlank()) return;
        LocalTime hi;
        try { hi = InputValidator.parseTime(sHi); }
        catch (IllegalArgumentException e) { System.out.println(e.getMessage()); return; }

        System.out.print("Hora Fim (HH:MM): ");
        String sHf = scanner.nextLine(); if (sHf.isBlank()) return;
        LocalTime hf;
        try { hf = InputValidator.parseTime(sHf); }
        catch (IllegalArgumentException e) { System.out.println(e.getMessage()); return; }

        Reserva r = reservaSvc.reservarArea(area, data, hi, hf);
        System.out.println(r != null ? "Reserva confirmada: " + r : "Horário indisponível!");
    }

    private static void listarReservas(ReservaService reservaSvc) {
        System.out.println("--- Reservas Registradas ---");
        List<Reserva> lista = reservaSvc.listarReservas();
        lista.forEach(System.out::println);
    }

    private static void reportarProblema(ManutencaoService manutSvc) {
        System.out.println("--- Reportar Problema (ENTER cancela) ---");
        System.out.print("Local: ");
        String local = scanner.nextLine(); if (local.isBlank()) return;
        System.out.print("Descrição: ");
        String desc = scanner.nextLine(); if (desc.isBlank()) return;
        System.out.print("Externo (s/n): ");
        boolean ext = scanner.nextLine().equalsIgnoreCase("s");
        var rm = manutSvc.reportarProblema(local, desc, ext);
        System.out.println("Protocolo: " + rm.getProtocolo());
    }

    private static void visualizarAssembleias(AssembleiaService assembleiaSvc) {
        System.out.println("--- Assembleias Agendadas ---");
        assembleiaSvc.listarAssembleias().forEach(System.out::println);
    }
}
