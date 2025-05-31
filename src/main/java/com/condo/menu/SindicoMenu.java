// src/main/java/com/condo/menu/SindicoMenu.java
package com.condo.menu;

import com.condo.domain.Comunicado;
import com.condo.domain.Condominium;
import com.condo.domain.Morador;
import com.condo.domain.ReportManutencao;
import com.condo.domain.Reserva;
import com.condo.domain.Sindico;
import com.condo.service.ComunicadoService;
import com.condo.service.ManutencaoService;
import com.condo.service.ReservaService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Scanner;
import java.util.InputMismatchException;

@Component
public class SindicoMenu {

    private static final Logger log = LoggerFactory.getLogger(SindicoMenu.class);
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");
    private static final DateTimeFormatter DATETIME_FORMATTER_DISPLAY = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    private final ReservaService reservaService;
    private final ManutencaoService manutencaoService;
    private final ComunicadoService comunicadoService;

    @Autowired
    public SindicoMenu(ReservaService reservaService, ManutencaoService manutencaoService, ComunicadoService comunicadoService) {
        this.reservaService = reservaService;
        this.manutencaoService = manutencaoService;
        this.comunicadoService = comunicadoService;
    }

    public void exibirMenu(Scanner scanner, Sindico sindicoLogado) {
        boolean continuar = true;
        while (continuar) {
            System.out.println("\n--- Menu do Síndico (" + sindicoLogado.getNome() + ") ---");
            System.out.println("1. Gerenciar Reservas de Áreas Comuns");
            System.out.println("2. Monitorar Solicitações de Manutenção");
            System.out.println("3. Criar Novo Comunicado");
            System.out.println("4. Listar Comunicados do Condomínio");
            System.out.println("5. Gerenciar Assembleias (Não implementado)");
            System.out.println("6. Visualizar Relatórios Financeiros (Não implementado)");
            System.out.println("0. Voltar (Logout)");
            System.out.print("Escolha uma opção: ");

            try {
                int opcao = lerInt(scanner);

                switch (opcao) {
                    case 1:
                        gerenciarReservas(scanner, sindicoLogado);
                        break;
                    case 2:
                        monitorarManutencoes(scanner, sindicoLogado);
                        break;
                    case 3:
                        criarNovoComunicado(scanner, sindicoLogado);
                        break;
                    case 4:
                        listarComunicadosDoCondominio(sindicoLogado);
                        break;
                    case 5:
                        System.out.println("Funcionalidade de Gerenciar Assembleias ainda não implementada.");
                        break;
                    case 6:
                        System.out.println("Funcionalidade de Relatórios Financeiros ainda não implementada.");
                        break;
                    case 0:
                        continuar = false;
                        System.out.println("Fazendo logout...");
                        break;
                    default:
                        System.out.println("Opção inválida. Tente novamente.");
                }
            } catch (Exception e) {
                log.error("Erro no menu do síndico: {}", e.getMessage(), e);
                System.err.println("Ocorreu um erro inesperado: " + e.getMessage() + ". Tente novamente.");
            }
        }
    }

    private void gerenciarReservas(Scanner scanner, Sindico sindicoLogado) {
        boolean subMenuContinuar = true;
        while (subMenuContinuar) {
            System.out.println("\n--- Gerenciar Reservas ---");
            System.out.println("1. Listar Todas as Reservas do Condomínio");
            System.out.println("2. Listar Reservas Pendentes do Condomínio");
            System.out.println("3. Aprovar Reserva");
            System.out.println("4. Rejeitar Reserva");
            System.out.println("0. Voltar ao Menu Principal do Síndico");
            System.out.print("Escolha uma opção: ");

            int opcaoReserva = lerInt(scanner);

            switch (opcaoReserva) {
                case 1:
                    listarTodasReservas(sindicoLogado);
                    break;
                case 2:
                    listarReservasPorStatus("PENDENTE", sindicoLogado);
                    break;
                case 3:
                    aprovarReserva(scanner, sindicoLogado);
                    break;
                case 4:
                    rejeitarReserva(scanner, sindicoLogado);
                    break;
                case 0:
                    subMenuContinuar = false;
                    break;
                default:
                    System.out.println("Opção inválida.");
            }
        }
    }

    private void imprimirDetalhesReserva(List<Reserva> reservas) {
        for (Reserva r : reservas) {
            Morador morador = r.getMorador();
            String nomeMorador = (morador != null) ? morador.getNome() : "Morador Desconhecido";
            String unidadeMorador = (morador != null && morador.getUnidade() != null) ? morador.getUnidade() : "N/A";

            System.out.println("ID: " + r.getId() + " | Área: " + r.getAreaComum().getNome() +
                    " | Morador: " + nomeMorador + " (Unid: " + unidadeMorador + ")" +
                    " | Data: " + r.getData().format(DATE_FORMATTER) +
                    " | Horário: " + r.getHoraInicio().format(TIME_FORMATTER) + " - "
                    + r.getHoraFim().format(TIME_FORMATTER) +
                    " | Status: " + r.getStatus());
        }
    }

    private void listarTodasReservas(Sindico sindicoLogado) {
        Condominium condominio = sindicoLogado.getCondominio();
        if (condominio == null) {
            System.err.println("Síndico não associado a um condomínio.");
            return;
        }
        List<Reserva> reservas = reservaService.listarTodasReservasPorCondominio(condominio);
        if (reservas.isEmpty()) {
            System.out.println("Nenhuma reserva encontrada para este condomínio.");
            return;
        }
        System.out.println("\n--- Todas as Reservas do Condomínio ---");
        imprimirDetalhesReserva(reservas);
    }

    private void listarReservasPorStatus(String status, Sindico sindicoLogado) {
        Condominium condominio = sindicoLogado.getCondominio();
        if (condominio == null) {
            System.err.println("Síndico não associado a um condomínio.");
            return;
        }
        List<Reserva> reservas = reservaService.listarReservasPendentesPorCondominioEStatus(condominio, status);
        if (reservas.isEmpty()) {
            System.out.println("Nenhuma reserva encontrada com o status: " + status);
            return;
        }
        System.out.println("\n--- Reservas com Status: " + status + " ---");
        imprimirDetalhesReserva(reservas);
    }

    private void aprovarReserva(Scanner scanner, Sindico sindicoLogado) {
        listarReservasPorStatus("PENDENTE", sindicoLogado);
        System.out.print("Digite o ID da reserva para APROVAR (ou 0 para cancelar): ");
        Long reservaId = lerLong(scanner);
        if (reservaId == null || reservaId == 0) {
            if (reservaId != null && reservaId == 0)
                System.out.println("Operação cancelada.");
            return;
        }

        try {
            Reserva reservaAprovada = reservaService.aprovarReserva(reservaId, sindicoLogado);
            System.out.println("Reserva ID " + reservaAprovada.getId() + " aprovada com sucesso.");
        } catch (IllegalArgumentException | IllegalStateException | SecurityException e) {
            System.err.println("Erro ao aprovar reserva: " + e.getMessage());
        }
    }

    private void rejeitarReserva(Scanner scanner, Sindico sindicoLogado) {
        listarReservasPorStatus("PENDENTE", sindicoLogado);
        System.out.print("Digite o ID da reserva para REJEITAR (ou 0 para cancelar): ");
        Long reservaId = lerLong(scanner);
        if (reservaId == null || reservaId == 0) {
            if (reservaId != null && reservaId == 0)
                System.out.println("Operação cancelada.");
            return;
        }

        System.out.print("Motivo da rejeição (opcional, pressione Enter para pular): ");
        String motivo = scanner.nextLine();

        try {
            // CHAMADA CORRETA:
            Reserva reservaRejeitada = reservaService.rejeitarReserva(reservaId, sindicoLogado, motivo);
            System.out.println("Reserva ID " + reservaRejeitada.getId() + " rejeitada com sucesso.");
        } catch (IllegalArgumentException | IllegalStateException | SecurityException e) {
            System.err.println("Erro ao rejeitar reserva: " + e.getMessage());
        }
    }

    private void monitorarManutencoes(Scanner scanner, Sindico sindicoLogado) {
        boolean subMenuContinuar = true;
        while (subMenuContinuar) {
            System.out.println("\n--- Monitorar Solicitações de Manutenção ---");
            System.out.println("1. Listar Todas as Solicitações");
            System.out.println("2. Listar Solicitações por Status");
            System.out.println("3. Atualizar Status de Solicitação");
            System.out.println("4. Adicionar Comentário à Solicitação");
            System.out.println("0. Voltar ao Menu Principal do Síndico");
            System.out.print("Escolha uma opção: ");

            int opcaoManut = lerInt(scanner);

            switch (opcaoManut) {
                case 1:
                    listarTodasManutencoes();
                    break;
                case 2:
                    System.out.print("Digite o status para filtrar (ex: ABERTO, EM_ANDAMENTO, CONCLUIDA): ");
                    String statusFiltro = scanner.nextLine().toUpperCase();
                    listarManutencoesPorStatus(statusFiltro);
                    break;
                case 3:
                    atualizarStatusManutencao(scanner, sindicoLogado);
                    break;
                case 4:
                    adicionarComentarioManutencao(scanner, sindicoLogado);
                    break;
                case 0:
                    subMenuContinuar = false;
                    break;
                default:
                    System.out.println("Opção inválida.");
            }
        }
    }

    private void imprimirDetalhesManutencao(List<ReportManutencao> reports) {
        for (ReportManutencao report : reports) {
            System.out.println("ID: " + report.getId() + " | Local: " + report.getLocal() +
                    " | Descrição: " + report.getDescricao() +
                    " | Status: " + report.getStatus() +
                    " | Criado em: " + report.getCriadoEm().format(DATETIME_FORMATTER_DISPLAY));
        }
    }

    private void listarTodasManutencoes() {
        List<ReportManutencao> reports = manutencaoService.listarTodosReports();
        if (reports.isEmpty()) {
            System.out.println("Nenhuma solicitação de manutenção encontrada.");
            return;
        }
        System.out.println("\n--- Todas as Solicitações de Manutenção ---");
        imprimirDetalhesManutencao(reports);
    }

    private void listarManutencoesPorStatus(String status) {
        List<ReportManutencao> reports = manutencaoService.listarReportsPorStatus(status);
        if (reports.isEmpty()) {
            System.out.println("Nenhuma solicitação de manutenção encontrada com o status: " + status);
            return;
        }
        System.out.println("\n--- Solicitações de Manutenção com Status: " + status + " ---");
        imprimirDetalhesManutencao(reports);
    }

    private void atualizarStatusManutencao(Scanner scanner, Sindico sindicoLogado) {
        listarTodasManutencoes();
        System.out.print("Digite o ID da solicitação para ATUALIZAR STATUS (ou 0 para cancelar): ");
        Long reportId = lerLong(scanner);
        if (reportId == null || reportId == 0) {
            if (reportId != null && reportId == 0)
                System.out.println("Operação cancelada.");
            return;
        }

        System.out.print("Novo status (ex: EM_ANDAMENTO, CONCLUIDA, CANCELADA_PELO_SINDICO): ");
        String novoStatus = scanner.nextLine().toUpperCase();
        System.out.print("Adicionar um comentário sobre a atualização (opcional, pressione Enter para pular): ");
        String comentario = scanner.nextLine();

        try {
            ReportManutencao reportAtualizado = manutencaoService.atualizarStatusManutencao(reportId, novoStatus,
                    comentario, sindicoLogado);
            System.out.println("Status da solicitação ID " + reportAtualizado.getId() + " atualizado para "
                    + reportAtualizado.getStatus());
        } catch (IllegalArgumentException | IllegalStateException e) {
            System.err.println("Erro ao atualizar status: " + e.getMessage());
        }
    }

    private void adicionarComentarioManutencao(Scanner scanner, Sindico sindicoLogado) {
        listarTodasManutencoes();
        System.out.print("Digite o ID da solicitação para ADICIONAR COMENTÁRIO (ou 0 para cancelar): ");
        Long reportId = lerLong(scanner);
        if (reportId == null || reportId == 0) {
            if (reportId != null && reportId == 0)
                System.out.println("Operação cancelada.");
            return;
        }

        System.out.print("Seu comentário: ");
        String comentario = scanner.nextLine();
        if (comentario.trim().isEmpty()) {
            System.out.println("Comentário não pode ser vazio.");
            return;
        }

        try {
            manutencaoService.adicionarComentarioManutencao(reportId, comentario, sindicoLogado);
            System.out.println("Comentário adicionado à solicitação ID " + reportId
                    + ". (Observação: persistência do comentário depende da implementação no service)");
        } catch (IllegalArgumentException e) {
            System.err.println("Erro ao adicionar comentário: " + e.getMessage());
        }
    }

    private void criarNovoComunicado(Scanner scanner, Sindico sindicoLogado) {
        System.out.println("\n--- Criar Novo Comunicado ---");

        Condominium condominioDoSindico = sindicoLogado.getCondominio();
        if (condominioDoSindico == null) {
            System.err.println("ERRO: Síndico não está associado a um condomínio. Não é possível criar comunicado.");
            log.warn("Tentativa de criar comunicado por síndico {} sem condomínio associado.", sindicoLogado.getEmail());
            return;
        }

        System.out.print("Título do comunicado: ");
        String titulo = scanner.nextLine();
        if (titulo.trim().isEmpty()) {
            System.out.println("O título não pode ser vazio.");
            return;
        }

        System.out.println("Conteúdo do comunicado (digite '~FIM' em uma nova linha para terminar):");
        StringBuilder conteudoBuilder = new StringBuilder();
        String linha;
        while (!(linha = scanner.nextLine()).equalsIgnoreCase("~FIM")) {
            conteudoBuilder.append(linha).append(System.lineSeparator());
        }
        String conteudo = conteudoBuilder.toString().trim();

        if (conteudo.isEmpty()) {
            System.out.println("O conteúdo não pode ser vazio.");
            return;
        }

        try {
            Comunicado novoComunicado = comunicadoService.criarComunicado(titulo, conteudo, sindicoLogado, condominioDoSindico);
            System.out.println("Comunicado criado com sucesso!");
            System.out.println("ID: " + novoComunicado.getId() + " | Título: " + novoComunicado.getTitulo());
        } catch (IllegalArgumentException | SecurityException e) {
            System.err.println("Erro ao criar comunicado: " + e.getMessage());
        } catch (Exception e) {
            log.error("Erro inesperado ao criar comunicado pelo síndico {}: {}", sindicoLogado.getEmail(), e.getMessage(), e);
            System.err.println("Ocorreu um erro inesperado ao criar o comunicado.");
        }
    }

    private void imprimirDetalhesComunicado(Comunicado comunicado) {
        System.out.println("--------------------------------------------------");
        System.out.println("ID: " + comunicado.getId());
        System.out.println("Título: " + comunicado.getTitulo());
        System.out.println("Publicado por: " + (comunicado.getSindico() != null ? comunicado.getSindico().getNome() : "N/A"));
        System.out.println("Data de Publicação: " + comunicado.getDataPublicacao().format(DATETIME_FORMATTER_DISPLAY));
        System.out.println("Conteúdo:\n" + comunicado.getConteudo());
        System.out.println("--------------------------------------------------");
    }

    private void listarComunicadosDoCondominio(Sindico sindicoLogado) {
        System.out.println("\n--- Comunicados do Condomínio ---");
        Condominium condominio = sindicoLogado.getCondominio();
        if (condominio == null) {
            System.err.println("Síndico não está associado a um condomínio.");
            return;
        }
        try {
            List<Comunicado> comunicados = comunicadoService.listarComunicadosPorCondominio(condominio);
            if (comunicados.isEmpty()) {
                System.out.println("Nenhum comunicado encontrado para este condomínio.");
            } else {
                comunicados.forEach(this::imprimirDetalhesComunicado);
            }
        } catch (IllegalArgumentException e) {
            System.err.println("Erro ao listar comunicados: " + e.getMessage());
        } catch (Exception e) {
            log.error("Erro inesperado ao listar comunicados do condomínio {} pelo síndico {}: {}",
                    condominio.getId(), sindicoLogado.getEmail(), e.getMessage(), e);
            System.err.println("Ocorreu um erro inesperado ao listar os comunicados.");
        }
    }

    private int lerInt(Scanner scanner) {
        try {
            String line = scanner.nextLine();
            if (line.isBlank()) {
                System.err.println("Entrada inválida. Por favor, insira um número inteiro.");
                return -99;
            }
            return Integer.parseInt(line);
        } catch (NumberFormatException e) {
            System.err.println("Entrada inválida. Por favor, insira um número inteiro.");
            return -99;
        }
    }

    private Long lerLong(Scanner scanner) {
        try {
            String line = scanner.nextLine();
            if (line.isBlank()) {
                System.err.println("Entrada inválida. Por favor, insira um número.");
                return null;
            }
            return Long.parseLong(line);
        } catch (NumberFormatException e) {
            System.err.println("Entrada inválida. Por favor, insira um número (long).");
            return null;
        }
    }
}