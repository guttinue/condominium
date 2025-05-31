// src/main/java/com/condo/menu/MoradorMenu.java
package com.condo.menu;

import com.condo.domain.AreaComum;
import com.condo.domain.Morador;
import com.condo.domain.Pagamento; // Importar Pagamento
import com.condo.domain.Reserva;
import com.condo.domain.ReportManutencao;
import com.condo.service.ManutencaoService;
import com.condo.service.PagamentoService; // Importar PagamentoService
import com.condo.service.ReservaService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;
import java.util.InputMismatchException;

@Component
public class MoradorMenu {

    private static final Logger log = LoggerFactory.getLogger(MoradorMenu.class);
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");

    private final ReservaService reservaService;
    private final ManutencaoService manutencaoService;
    private final PagamentoService pagamentoService; // Injetar PagamentoService

    @Autowired
    public MoradorMenu(ReservaService reservaService,
                       ManutencaoService manutencaoService,
                       PagamentoService pagamentoService) { // Adicionar PagamentoService ao construtor
        this.reservaService = reservaService;
        this.manutencaoService = manutencaoService;
        this.pagamentoService = pagamentoService;
    }

    public void exibirMenu(Scanner scanner, Morador moradorLogado) {
        boolean continuar = true;
        while (continuar) {
            System.out.println("\n--- Menu do Morador (" + moradorLogado.getNome() + " - Unidade: " + moradorLogado.getUnidade() + ") ---");
            System.out.println("1. Ver Áreas Comuns Disponíveis");
            System.out.println("2. Fazer Reserva de Área Comum");
            System.out.println("3. Minhas Reservas");
            System.out.println("4. Cancelar Reserva");
            System.out.println("5. Reportar Problema de Manutenção");
            System.out.println("6. Ver Minhas Taxas Pendentes"); // Nova opção
            System.out.println("0. Voltar (Logout)");
            System.out.print("Escolha uma opção: ");

            try {
                int opcao = lerInt(scanner);

                switch (opcao) {
                    case 1:
                        verAreasComuns();
                        break;
                    case 2:
                        fazerReserva(scanner, moradorLogado);
                        break;
                    case 3:
                        minhasReservas(moradorLogado);
                        break;
                    case 4:
                        cancelarReserva(scanner, moradorLogado);
                        break;
                    case 5:
                        reportarManutencao(scanner, moradorLogado);
                        break;
                    case 6: // Novo case
                        verMinhasTaxasPendentes(moradorLogado);
                        break;
                    case 0:
                        continuar = false;
                        System.out.println("Fazendo logout...");
                        break;
                    default:
                        System.out.println("Opção inválida. Tente novamente.");
                }
            } catch (Exception e) {
                log.error("Erro no menu do morador: {}", e.getMessage(), e);
                System.err.println("Ocorreu um erro inesperado no menu: " + e.getMessage() + ". Verifique os logs.");
            }
        }
    }

    // ... (métodos existentes: verAreasComuns, fazerReserva, minhasReservas, cancelarReserva, reportarManutencao, lerInt, lerLong) ...

    // NOVO MÉTODO
    private void verMinhasTaxasPendentes(Morador moradorLogado) {
        System.out.println("\n--- Minhas Taxas Pendentes ---");
        try {
            List<Pagamento> taxasPendentes = pagamentoService.listarTaxasPendentesPorMorador(moradorLogado);

            if (taxasPendentes.isEmpty()) {
                System.out.println("Você não possui nenhuma taxa pendente no momento. Parabéns!");
            } else {
                System.out.println("As seguintes taxas estão aguardando pagamento:");
                System.out.println("------------------------------------------------------------------------------------");
                System.out.printf("%-5s | %-30s | %-10s | %-15s | %-10s\n", "ID", "Descrição", "Valor", "Vencimento", "Status");
                System.out.println("------------------------------------------------------------------------------------");
                for (Pagamento taxa : taxasPendentes) {
                    System.out.printf("%-5d | %-30.30s | %10.2f | %-15s | %-10s\n",
                            taxa.getId(),
                            taxa.getDescricao(),
                            taxa.getValor(),
                            taxa.getDataVencimento().format(DATE_FORMATTER),
                            taxa.getStatus());
                }
                System.out.println("------------------------------------------------------------------------------------");
            }
        } catch (IllegalArgumentException e) {
            System.err.println("Erro ao buscar taxas: " + e.getMessage());
        } catch (Exception e) {
            log.error("Erro inesperado ao listar taxas pendentes para o morador {}:", moradorLogado.getId(), e);
            System.err.println("Ocorreu um erro inesperado ao buscar suas taxas pendentes: " + e.getMessage());
        }
    }

    // Métodos auxiliares existentes
    private void verAreasComuns() {
        System.out.println("\n--- Áreas Comuns ---");
        List<AreaComum> areas = reservaService.listarAreasComuns();
        if (areas.isEmpty()) {
            System.out.println("Nenhuma área comum cadastrada no momento.");
        } else {
            areas.forEach(area -> System.out.println(
                    "ID: " + area.getId() + " | Nome: " + area.getNome() +
                            " | Regras: " + (area.getRegrasUso() != null && !area.getRegrasUso().isEmpty() ? area.getRegrasUso() : "Nenhuma regra específica.")));
        }
    }

    private void fazerReserva(Scanner scanner, Morador moradorLogado) {
        System.out.println("\n--- Fazer Reserva ---");
        verAreasComuns();
        List<AreaComum> areasDisponiveis = reservaService.listarAreasComuns();
        if (areasDisponiveis.isEmpty()) {
            System.out.println("Não há áreas comuns para reservar no momento.");
            return;
        }

        System.out.print("Digite o ID da Área Comum desejada (ou 0 para cancelar): ");
        Long areaId = lerLong(scanner);
        if (areaId == null || areaId == 0) {
            if (areaId != null && areaId == 0) System.out.println("Operação cancelada.");
            return;
        }

        Optional<AreaComum> areaOpt = reservaService.buscarAreaComumPorId(areaId);
        if (areaOpt.isEmpty()) {
            System.out.println("Área comum com ID " + areaId + " não encontrada.");
            return;
        }
        System.out.println("Área selecionada: " + areaOpt.get().getNome());

        LocalDate dataReserva = null;
        while (dataReserva == null) {
            System.out.print("Data da reserva (dd/MM/yyyy) (ou 'cancelar'): ");
            String dataStr = scanner.nextLine();
            if ("cancelar".equalsIgnoreCase(dataStr)) { System.out.println("Operação cancelada."); return; }
            try {
                dataReserva = LocalDate.parse(dataStr, DATE_FORMATTER);
                if (dataReserva.isBefore(LocalDate.now())) {
                    System.out.println("A data da reserva não pode ser no passado. Tente novamente.");
                    dataReserva = null;
                }
            } catch (DateTimeParseException e) {
                System.out.println("Formato de data inválido. Use dd/MM/yyyy. Tente novamente.");
            }
        }

        LocalTime horaInicio = null;
        while (horaInicio == null) {
            System.out.print("Hora de início (HH:mm) (ou 'cancelar'): ");
            String horaInicioStr = scanner.nextLine();
            if ("cancelar".equalsIgnoreCase(horaInicioStr)) { System.out.println("Operação cancelada."); return; }
            try {
                horaInicio = LocalTime.parse(horaInicioStr, TIME_FORMATTER);
            } catch (DateTimeParseException e) {
                System.out.println("Formato de hora inválido. Use HH:mm. Tente novamente.");
            }
        }

        LocalTime horaFim = null;
        while (horaFim == null) {
            System.out.print("Hora de término (HH:mm) (ou 'cancelar'): ");
            String horaFimStr = scanner.nextLine();
            if ("cancelar".equalsIgnoreCase(horaFimStr)) { System.out.println("Operação cancelada."); return; }
            try {
                horaFim = LocalTime.parse(horaFimStr, TIME_FORMATTER);
                if (horaFim.isBefore(horaInicio) || horaFim.equals(horaInicio)) {
                    System.out.println("A hora de término deve ser posterior à hora de início. Tente novamente.");
                    horaFim = null;
                }
            } catch (DateTimeParseException e) {
                System.out.println("Formato de hora inválido. Use HH:mm. Tente novamente.");
            }
        }

        try {
            Reserva novaReserva = reservaService.criarReserva(moradorLogado, areaId, dataReserva, horaInicio, horaFim);
            System.out.println("Solicitação de reserva enviada com sucesso! ID da Reserva: " + novaReserva.getId() + ", Status: " + novaReserva.getStatus());
            System.out.println("Área: " + novaReserva.getAreaComum().getNome() + ", Data: " + novaReserva.getData().format(DATE_FORMATTER) +
                    " das " + novaReserva.getHoraInicio().format(TIME_FORMATTER) + " às " + novaReserva.getHoraFim().format(TIME_FORMATTER));
            System.out.println("Aguarde a aprovação do síndico.");
        } catch (IllegalArgumentException | IllegalStateException e) {
            System.out.println("Erro ao criar reserva: " + e.getMessage());
        }
    }

    private void minhasReservas(Morador moradorLogado) {
        System.out.println("\n--- Minhas Reservas ---");
        List<Reserva> reservas = reservaService.listarReservasPorMorador(moradorLogado);
        if (reservas.isEmpty()) {
            System.out.println("Você não possui nenhuma reserva no momento.");
        } else {
            reservas.forEach(r -> System.out.println(
                    "ID: " + r.getId() + " | Área: " + r.getAreaComum().getNome() +
                            " | Data: " + r.getData().format(DATE_FORMATTER) +
                            " | Horário: " + r.getHoraInicio().format(TIME_FORMATTER) + " - " + r.getHoraFim().format(TIME_FORMATTER) +
                            " | Status: " + r.getStatus()));
        }
    }

    private void cancelarReserva(Scanner scanner, Morador moradorLogado) {
        System.out.println("\n--- Cancelar Reserva ---");
        minhasReservas(moradorLogado);
        List<Reserva> reservasAtivas = reservaService.listarReservasPorMorador(moradorLogado).stream()
                .filter(r -> "PENDENTE".equalsIgnoreCase(r.getStatus()) || "APROVADA".equalsIgnoreCase(r.getStatus()))
                .toList();

        if (reservasAtivas.isEmpty()) {
            System.out.println("Você não possui reservas ativas para cancelar.");
            return;
        }

        System.out.print("Digite o ID da Reserva que deseja cancelar (ou 0 para voltar): ");
        Long reservaId = lerLong(scanner);
        if (reservaId == null || reservaId == 0) {
            if (reservaId != null && reservaId == 0) System.out.println("Operação cancelada.");
            return;
        }

        try {
            if (reservaService.cancelarReserva(reservaId, moradorLogado)) {
                System.out.println("Reserva ID " + reservaId + " cancelada com sucesso.");
            } else {
                System.out.println("Reserva ID " + reservaId + " não encontrada ou não pôde ser cancelada.");
            }
        } catch (IllegalArgumentException | IllegalStateException | SecurityException e) {
            System.out.println("Erro ao cancelar reserva: " + e.getMessage());
        }
    }

    private void reportarManutencao(Scanner scanner, Morador moradorLogado) {
        System.out.println("\n--- Reportar Problema de Manutenção ---");
        System.out.print("Local do problema (ex: Piscina, Elevador Bloco A): ");
        String local = scanner.nextLine();
        System.out.print("Descrição do problema: ");
        String descricao = scanner.nextLine();

        if (local.trim().isEmpty() || descricao.trim().isEmpty()) {
            System.out.println("Local e descrição não podem ser vazios.");
            return;
        }

        try {
            ReportManutencao report = manutencaoService.criarReportManutencao(moradorLogado, local, descricao);
            System.out.println("Problema reportado com sucesso! Protocolo (ID): " + report.getId());
            System.out.println("Local: " + report.getLocal() + ", Descrição: " + report.getDescricao() + ", Status: " + report.getStatus());
        } catch (IllegalArgumentException e) {
            System.out.println("Erro ao reportar problema: " + e.getMessage());
        } catch (Exception e) {
            log.error("Erro inesperado ao reportar manutenção:", e);
            System.out.println("Ocorreu um erro inesperado ao reportar o problema.");
        }
    }

    private int lerInt(Scanner scanner) {
        try {
            int valor = scanner.nextInt();
            scanner.nextLine();
            return valor;
        } catch (InputMismatchException e) {
            System.err.println("Entrada inválida. Por favor, insira um número inteiro.");
            log.warn("Entrada inválida no menu (esperado int): {}", scanner.nextLine());
            return -1;
        }
    }

    private Long lerLong(Scanner scanner) {
        try {
            long valor = scanner.nextLong();
            scanner.nextLine();
            return valor;
        } catch (InputMismatchException e) {
            System.err.println("Entrada inválida. Por favor, insira um número (ID).");
            log.warn("Entrada inválida no menu (esperado long): {}", scanner.nextLine());
            return null;
        }
    }
}
