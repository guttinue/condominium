package com.condo.menu;

import com.condo.domain.AreaComum;
import com.condo.domain.Comunicado;
import com.condo.domain.Condominium;
import com.condo.domain.Morador;
import com.condo.domain.Pagamento;
import com.condo.domain.ReportManutencao;
import com.condo.domain.Reserva;
import com.condo.domain.Veiculo; // Adicionado para funcionalidades de veículo
import com.condo.domain.Visitante; // Adicionado para funcionalidades de visitante (se você as adicionou aqui)
import com.condo.service.ComunicadoService;
import com.condo.service.ManutencaoService;
import com.condo.service.PagamentoService;
import com.condo.service.ReservaService;
import com.condo.service.VeiculoService; // Adicionado para funcionalidades de veículo
import com.condo.service.VisitanteService; // Adicionado para funcionalidades de visitante (se você as adicionou aqui)

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime; // Para Visitante
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;

@Component
public class MoradorMenu {

    private static final Logger log = LoggerFactory.getLogger(MoradorMenu.class);
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");
    private static final DateTimeFormatter DATETIME_FORMATTER_DISPLAY_COMUNICADO = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    private static final DateTimeFormatter DATETIME_FORMATTER_INPUT_VISITANTE = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"); // Para entrada de data/hora de visitante

    private final ComunicadoService comunicadoService;
    private final ReservaService reservaService;
    private final ManutencaoService manutencaoService;
    private final PagamentoService pagamentoService;
    private final VeiculoService veiculoService;     // Adicionado
    private final VisitanteService visitanteService; // Adicionado

    @Autowired
    public MoradorMenu(ComunicadoService comunicadoService, ReservaService reservaService,
                       ManutencaoService manutencaoService, PagamentoService pagamentoService,
                       VeiculoService veiculoService, VisitanteService visitanteService) { // Serviços adicionados
        this.comunicadoService = comunicadoService;
        this.reservaService = reservaService;
        this.manutencaoService = manutencaoService;
        this.pagamentoService = pagamentoService;
        this.veiculoService = veiculoService;         // Adicionado
        this.visitanteService = visitanteService;     // Adicionado
    }

    public void exibirMenu(Scanner scanner, Morador moradorLogado) {
        if (moradorLogado == null || moradorLogado.getCondominio() == null) {
            System.err.println("Erro: Informações do morador ou condomínio não disponíveis.");
            log.error("Tentativa de exibir menu do morador sem moradorLogado ou condominio associado. Morador: {}",
                    (moradorLogado != null ? moradorLogado.getEmail() : "null"));
            return;
        }
        final Condominium condominioDoMorador = moradorLogado.getCondominio();
        final Long condominioId = condominioDoMorador.getId();

        boolean continuar = true;
        while (continuar) {
            System.out.println("\n--- Menu do Morador (" + moradorLogado.getNome() + " - Unidade: " + moradorLogado.getUnidade() + ") ---");
            System.out.println("Condomínio: " + condominioDoMorador.getNome());
            System.out.println("1. Ver Áreas Comuns Disponíveis");
            System.out.println("2. Fazer Reserva de Área Comum");
            System.out.println("3. Minhas Reservas");
            System.out.println("4. Cancelar Reserva");
            System.out.println("5. Reportar Problema de Manutenção");
            System.out.println("6. Ver Minhas Taxas/Pagamentos");
            System.out.println("7. Ver Comunicados do Condomínio");
            System.out.println("8. Gerenciar Meus Visitantes"); // Nova opção
            System.out.println("9. Gerenciar Meus Veículos");   // Nova opção
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
                    case 6:
                        verMinhasTaxas(moradorLogado);
                        break;
                    case 7:
                        visualizarComunicados(condominioDoMorador);
                        break;
                    case 8: // Novo
                        gerenciarVisitantes(scanner, moradorLogado);
                        break;
                    case 9: // Novo
                        gerenciarMeusVeiculos(scanner, moradorLogado);
                        break;
                    case 0:
                        continuar = false;
                        System.out.println("Fazendo logout...");
                        break;
                    default:
                        System.out.println("Opção inválida. Tente novamente.");
                }
            } catch (Exception e) {
                log.error("Erro no menu do morador (ID: {}): {}", moradorLogado.getId(), e.getMessage(), e);
                System.err.println("Ocorreu um erro inesperado no menu: " + e.getMessage() + ". Verifique os logs.");
            }
        }
    }

    private void verMinhasTaxas(Morador moradorLogado) {
        System.out.println("\n--- Minhas Taxas/Pagamentos ---");
        try {
            List<Pagamento> taxasPendentes = pagamentoService.listarTaxasPendentesPorMorador(moradorLogado);
            if (taxasPendentes.isEmpty()) {
                System.out.println("Você não possui nenhuma taxa pendente no momento. Parabéns!");
            } else {
                System.out.println("Suas taxas pendentes são:");
                imprimirListaDePagamentos(taxasPendentes);
            }
        } catch (IllegalArgumentException e) {
            System.err.println("Erro ao buscar suas taxas: " + e.getMessage());
        } catch (Exception e) {
            log.error("Erro inesperado ao listar taxas para o morador ID {}:", moradorLogado.getId(), e);
            System.err.println("Ocorreu um erro inesperado ao buscar suas taxas: " + e.getMessage());
        }
    }

    private void imprimirListaDePagamentos(List<Pagamento> pagamentos) {
        if (pagamentos == null || pagamentos.isEmpty()) {
            System.out.println("Nenhum registro de pagamento para exibir.");
            return;
        }
        System.out.println("-------------------------------------------------------------------------------------------------");
        System.out.printf("%-5s | %-25.25s | %-10s | %-12s | %-12s | %-10s\n",
                "ID", "Descrição", "Valor", "Vencimento", "Dt. Pagto", "Status");
        System.out.println("-------------------------------------------------------------------------------------------------");
        for (Pagamento taxa : pagamentos) {
            System.out.printf("%-5d | %-25.25s | R$%8.2f | %-12s | %-12s | %-10s\n",
                    taxa.getId(),
                    taxa.getDescricao() != null ? taxa.getDescricao() : "N/A",
                    taxa.getValor(),
                    taxa.getDataVencimento().format(DATE_FORMATTER),
                    (taxa.getDataPagamento() != null ? taxa.getDataPagamento().format(DATE_FORMATTER) : "N/A"),
                    taxa.getStatus());
        }
        System.out.println("-------------------------------------------------------------------------------------------------");
    }

    private void verAreasComuns() {
        System.out.println("\n--- Áreas Comuns Disponíveis ---");
        List<AreaComum> areas = reservaService.listarAreasComuns();
        if (areas.isEmpty()) {
            System.out.println("Nenhuma área comum cadastrada no momento.");
        } else {
            areas.forEach(area -> System.out.println(
                    "ID: " + area.getId() + " | Nome: " + area.getNome() +
                            " | Regras: " + (area.getRegrasUso() != null && !area.getRegrasUso().isEmpty() ? area.getRegrasUso() : "Nenhuma.")));
        }
    }

    private void fazerReserva(Scanner scanner, Morador moradorLogado) {
        System.out.println("\n--- Fazer Reserva ---");
        verAreasComuns();
        if (reservaService.listarAreasComuns().isEmpty()) {
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
            System.err.println("Área comum com ID " + areaId + " não encontrada.");
            return;
        }
        AreaComum areaSelecionada = areaOpt.get();
        System.out.println("Área selecionada: " + areaSelecionada.getNome());
        if (areaSelecionada.getRegrasUso() != null && !areaSelecionada.getRegrasUso().isEmpty()){
            System.out.println("Regras de uso: " + areaSelecionada.getRegrasUso());
        }
        LocalDate dataReserva = null;
        while (dataReserva == null) {
            System.out.print("Data da reserva (dd/MM/yyyy) (ou 'cancelar'): ");
            String dataStr = scanner.nextLine().trim();
            if ("cancelar".equalsIgnoreCase(dataStr)) { System.out.println("Operação cancelada."); return; }
            if (dataStr.isEmpty()) { System.err.println("Data não pode ser vazia. Tente novamente."); continue;}
            try {
                dataReserva = LocalDate.parse(dataStr, DATE_FORMATTER);
                if (dataReserva.isBefore(LocalDate.now())) {
                    System.err.println("A data da reserva não pode ser no passado. Tente novamente.");
                    dataReserva = null;
                }
            } catch (DateTimeParseException e) {
                System.err.println("Formato de data inválido. Use dd/MM/yyyy. Tente novamente.");
            }
        }
        LocalTime horaInicio = null;
        while (horaInicio == null) {
            System.out.print("Hora de início (HH:mm) (ou 'cancelar'): ");
            String horaInicioStr = scanner.nextLine().trim();
            if ("cancelar".equalsIgnoreCase(horaInicioStr)) { System.out.println("Operação cancelada."); return; }
            if (horaInicioStr.isEmpty()) { System.err.println("Hora de início não pode ser vazia. Tente novamente."); continue;}
            try {
                horaInicio = LocalTime.parse(horaInicioStr, TIME_FORMATTER);
            } catch (DateTimeParseException e) {
                System.err.println("Formato de hora inválido. Use HH:mm. Tente novamente.");
            }
        }
        LocalTime horaFim = null;
        while (horaFim == null) {
            System.out.print("Hora de término (HH:mm) (ou 'cancelar'): ");
            String horaFimStr = scanner.nextLine().trim();
            if ("cancelar".equalsIgnoreCase(horaFimStr)) { System.out.println("Operação cancelada."); return; }
            if (horaFimStr.isEmpty()) { System.err.println("Hora de término não pode ser vazia. Tente novamente."); continue;}
            try {
                horaFim = LocalTime.parse(horaFimStr, TIME_FORMATTER);
                if (horaFim.isBefore(horaInicio) || horaFim.equals(horaInicio)) {
                    System.err.println("A hora de término deve ser posterior à hora de início. Tente novamente.");
                    horaFim = null;
                }
            } catch (DateTimeParseException e) {
                System.err.println("Formato de hora inválido. Use HH:mm. Tente novamente.");
            }
        }
        try {
            Reserva novaReserva = reservaService.criarReserva(moradorLogado, areaId, dataReserva, horaInicio, horaFim);
            System.out.println("Solicitação de reserva enviada com sucesso! ID da Reserva: " + novaReserva.getId() + ", Status: " + novaReserva.getStatus());
            System.out.println("Área: " + novaReserva.getAreaComum().getNome() + ", Data: " + novaReserva.getData().format(DATE_FORMATTER) +
                    " das " + novaReserva.getHoraInicio().format(TIME_FORMATTER) + " às " + novaReserva.getHoraFim().format(TIME_FORMATTER));
            System.out.println("Aguarde a aprovação do síndico.");
        } catch (IllegalArgumentException | IllegalStateException e) {
            System.err.println("Erro ao criar reserva: " + e.getMessage());
        } catch (Exception e) {
            log.error("Erro inesperado ao criar reserva para morador ID {} e area ID {}: {}", moradorLogado.getId(), areaId, e.getMessage(), e);
            System.err.println("Ocorreu um erro inesperado ao criar a reserva.");
        }
    }

    private void minhasReservas(Morador moradorLogado) {
        System.out.println("\n--- Minhas Reservas ---");
        List<Reserva> reservas = reservaService.listarReservasPorMorador(moradorLogado);
        if (reservas.isEmpty()) {
            System.out.println("Você não possui nenhuma reserva no momento.");
        } else {
            System.out.println("------------------------------------------------------------------------------------------------------");
            System.out.printf("%-5s | %-25.25s | %-12s | %-15s | %-10s\n", "ID", "Área Comum", "Data", "Horário", "Status");
            System.out.println("------------------------------------------------------------------------------------------------------");
            for (Reserva r : reservas) {
                System.out.printf("%-5d | %-25.25s | %-12s | %s - %s | %-10s\n",
                        r.getId(),
                        r.getAreaComum().getNome(),
                        r.getData().format(DATE_FORMATTER),
                        r.getHoraInicio().format(TIME_FORMATTER),
                        r.getHoraFim().format(TIME_FORMATTER),
                        r.getStatus());
            }
            System.out.println("------------------------------------------------------------------------------------------------------");
        }
    }

    private void cancelarReserva(Scanner scanner, Morador moradorLogado) {
        System.out.println("\n--- Cancelar Reserva ---");
        minhasReservas(moradorLogado);
        List<Reserva> reservasCancelaveis = reservaService.listarReservasPorMorador(moradorLogado).stream()
                .filter(r -> "PENDENTE".equalsIgnoreCase(r.getStatus()) || "APROVADA".equalsIgnoreCase(r.getStatus()))
                .toList();
        if (reservasCancelaveis.isEmpty()) {
            System.out.println("Você não possui reservas que possam ser canceladas no momento (status PENDENTE ou APROVADA).");
            return;
        }
        System.out.print("Digite o ID da Reserva que deseja cancelar (ou 0 para voltar): ");
        Long reservaId = lerLong(scanner);
        if (reservaId == null || reservaId == 0) {
            if (reservaId != null && reservaId == 0) System.out.println("Operação cancelada.");
            return;
        }
        try {
            reservaService.cancelarReserva(reservaId, moradorLogado);
            System.out.println("Reserva ID " + reservaId + " cancelada com sucesso ou solicitação de cancelamento enviada.");
        } catch (IllegalArgumentException | IllegalStateException | SecurityException e) {
            System.err.println("Erro ao cancelar reserva: " + e.getMessage());
        } catch (Exception e) {
            log.error("Erro inesperado ao cancelar reserva ID {} para morador {}: {}", reservaId, moradorLogado.getId(), e.getMessage(), e);
            System.err.println("Ocorreu um erro inesperado ao cancelar a reserva.");
        }
    }

    private void reportarManutencao(Scanner scanner, Morador moradorLogado) {
        System.out.println("\n--- Reportar Problema de Manutenção ---");
        System.out.print("Local do problema (ex: Piscina, Elevador Bloco A): ");
        String local = scanner.nextLine().trim();
        System.out.print("Descrição do problema: ");
        String descricao = scanner.nextLine().trim();
        if (local.isEmpty() || descricao.isEmpty()) {
            System.err.println("Local e descrição não podem ser vazios.");
            return;
        }
        try {
            ReportManutencao report = manutencaoService.criarReportManutencao(moradorLogado, local, descricao);
            System.out.println("Problema reportado com sucesso! Protocolo (ID): " + report.getId());
            System.out.println("Local: " + report.getLocal() + ", Descrição: " + report.getDescricao() + ", Status: " + report.getStatus());
        } catch (IllegalArgumentException e) {
            System.err.println("Erro ao reportar problema: " + e.getMessage());
        } catch (Exception e) {
            log.error("Erro inesperado ao reportar manutenção por morador {}: {}", moradorLogado.getId(), e.getMessage(), e);
            System.err.println("Ocorreu um erro inesperado ao reportar o problema.");
        }
    }

    private void visualizarComunicados(Condominium condominio) {
        System.out.println("\n--- Comunicados do Condomínio ---");
        try {
            List<Comunicado> comunicados = comunicadoService.listarComunicadosPorCondominio(condominio);
            if (comunicados.isEmpty()) {
                System.out.println("Nenhum comunicado encontrado para este condomínio.");
                return;
            }
            System.out.println("------------------------------------------------------------------------------------");
            for (Comunicado com : comunicados) {
                System.out.println("Título: " + com.getTitulo());
                System.out.println("Publicado em: " + (com.getDataPublicacao() != null ? com.getDataPublicacao().format(DATETIME_FORMATTER_DISPLAY_COMUNICADO) : "N/A"));
                System.out.println("Por: " + (com.getSindico() != null && com.getSindico().getNome() != null ? com.getSindico().getNome() : "Administração"));
                System.out.println("Conteúdo:\n" + com.getConteudo());
                System.out.println("------------------------------------------------------------------------------------");
            }
        } catch (IllegalArgumentException e) {
            System.err.println("Erro ao listar comunicados: " + e.getMessage());
        } catch (Exception e) {
            log.error("Erro inesperado ao listar comunicados para condominio {}: {}", (condominio != null ? condominio.getNome() : "ID Desconhecido"), e.getMessage(), e);
            System.err.println("Ocorreu um erro inesperado ao listar os comunicados.");
        }
    }

    // --- MÉTODOS PARA GERENCIAR VISITANTES ---
    private void gerenciarVisitantes(Scanner scanner, Morador moradorLogado) {
        boolean subMenuContinuar = true;
        while(subMenuContinuar) {
            System.out.println("\n--- Gerenciar Visitantes (Morador: " + moradorLogado.getNome() + ") ---");
            System.out.println("1. Registrar Novo Visitante");
            System.out.println("2. Listar Meus Visitantes Agendados (Status 'ESPERADO')");
            System.out.println("3. Listar Todos os Meus Visitantes Registrados");
            System.out.println("0. Voltar ao Menu Principal do Morador");
            System.out.print("Escolha uma opção: ");
            int escolha = lerInt(scanner);

            switch(escolha) {
                case 1:
                    registrarNovoVisitantePeloMorador(scanner, moradorLogado);
                    break;
                case 2:
                    listarVisitantesEsperadosPeloMorador(moradorLogado);
                    break;
                case 3:
                    listarTodosVisitantesPeloMorador(moradorLogado);
                    break;
                case 0:
                    subMenuContinuar = false;
                    break;
                default:
                    System.out.println("Opção inválida.");
            }
        }
    }

    private void registrarNovoVisitantePeloMorador(Scanner scanner, Morador moradorLogado) {
        System.out.println("\n--- Registrar Novo Visitante ---");
        System.out.print("Nome completo do visitante: ");
        String nomeVisitante = scanner.nextLine().trim();
        if (nomeVisitante.isEmpty()){ System.err.println("Nome do visitante é obrigatório."); return; }

        System.out.print("Data e Hora PREVISTA da visita (dd/MM/yyyy HH:mm): ");
        LocalDateTime dataHoraVisita;
        try {
            String dataHoraStr = scanner.nextLine().trim();
            if (dataHoraStr.isEmpty()){ System.err.println("Data e hora da visita são obrigatórias."); return; }
            dataHoraVisita = LocalDateTime.parse(dataHoraStr, DATETIME_FORMATTER_INPUT_VISITANTE);
        } catch (DateTimeParseException e) {
            System.err.println("Formato de data/hora inválido. Use dd/MM/yyyy HH:mm.");
            return;
        }

        String unidadeDestino = moradorLogado.getUnidade();
        System.out.println("Unidade de destino: " + unidadeDestino);

        System.out.print("Documento do visitante (RG/CPF - opcional): ");
        String documento = scanner.nextLine().trim();
        System.out.print("Placa do veículo (opcional): ");
        String placa = scanner.nextLine().trim();

        try {
            if (moradorLogado.getCondominio() == null) {
                System.err.println("ERRO: Você não está associado a um condomínio. Não é possível registrar visitante.");
                return;
            }
            Long condominioId = moradorLogado.getCondominio().getId();

            Visitante novoVisitante = visitanteService.registrarVisitante(
                    moradorLogado.getId(), nomeVisitante, dataHoraVisita, unidadeDestino,
                    documento.isEmpty() ? null : documento,
                    placa.isEmpty() ? null : placa,
                    condominioId);
            System.out.println("Visitante '" + novoVisitante.getNomeVisitante() + "' registrado com sucesso para " +
                    dataHoraVisita.format(DATETIME_FORMATTER_INPUT_VISITANTE) + "!");
            imprimirDetalhesBasicosVisitante(novoVisitante);
        } catch (IllegalArgumentException e) {
            System.err.println("Erro ao registrar visitante: " + e.getMessage());
        } catch (Exception e) {
            log.error("Erro inesperado ao registrar visitante para morador {}: {}", moradorLogado.getId(), e.getMessage(), e);
            System.err.println("Ocorreu um erro inesperado ao registrar o visitante.");
        }
    }

    private void imprimirDetalhesBasicosVisitante(Visitante v) {
        System.out.println(String.format("ID: %d | Nome: %s | Previsto para: %s | Unidade: %s | Status: %s",
                v.getId(), v.getNomeVisitante(),
                v.getDataHoraVisitaPrevista().format(DATETIME_FORMATTER_INPUT_VISITANTE),
                v.getUnidadeDestino(), v.getStatusVisita()
        ));
    }

    private void listarVisitantesEsperadosPeloMorador(Morador moradorLogado) {
        System.out.println("\n--- Meus Visitantes Agendados (Status 'ESPERADO') ---");
        try {
            List<Visitante> visitantes = visitanteService.listarVisitantesEsperadosPorMorador(moradorLogado.getId());
            if (visitantes.isEmpty()) {
                System.out.println("Nenhum visitante com status 'ESPERADO' agendado.");
            } else {
                visitantes.forEach(this::imprimirDetalhesBasicosVisitante);
            }
        } catch (IllegalArgumentException e) {
            System.err.println("Erro ao listar visitantes esperados: " + e.getMessage());
        }
    }

    private void listarTodosVisitantesPeloMorador(Morador moradorLogado) {
        System.out.println("\n--- Todos os Meus Visitantes Registrados ---");
        try {
            List<Visitante> visitantes = visitanteService.listarTodosVisitantesPorMorador(moradorLogado.getId());
            if (visitantes.isEmpty()) {
                System.out.println("Nenhum visitante registrado por você.");
            } else {
                visitantes.forEach(this::imprimirDetalhesBasicosVisitante);
            }
        } catch (IllegalArgumentException e) {
            System.err.println("Erro ao listar todos os seus visitantes: " + e.getMessage());
        }
    }

    // --- MÉTODOS PARA GERENCIAR VEÍCULOS ---
    private void gerenciarMeusVeiculos(Scanner scanner, Morador moradorLogado) {
        boolean subMenuContinuar = true;
        while(subMenuContinuar) {
            System.out.println("\n--- Meus Veículos (" + moradorLogado.getUnidade() + ") ---");
            System.out.println("1. Cadastrar Novo Veículo");
            System.out.println("2. Listar Meus Veículos");
            System.out.println("3. Remover Veículo");
            System.out.println("0. Voltar ao Menu Principal");
            System.out.print("Escolha uma opção: ");
            int escolha = lerInt(scanner);

            switch(escolha) {
                case 1:
                    cadastrarNovoVeiculoPeloMorador(scanner, moradorLogado);
                    break;
                case 2:
                    listarMeusVeiculosPeloMorador(moradorLogado);
                    break;
                case 3:
                    removerVeiculoPeloMorador(scanner, moradorLogado);
                    break;
                case 0:
                    subMenuContinuar = false;
                    break;
                default:
                    System.out.println("Opção inválida.");
            }
        }
    }

    private void cadastrarNovoVeiculoPeloMorador(Scanner scanner, Morador moradorLogado) {
        System.out.println("\n--- Cadastrar Novo Veículo ---");
        System.out.print("Placa (ex: ABC1D23): ");
        String placa = scanner.nextLine().trim().toUpperCase();
        if (placa.isEmpty()){ System.err.println("Placa é obrigatória."); return; }

        System.out.print("Modelo (ex: Honda Civic): ");
        String modelo = scanner.nextLine().trim();
        if (modelo.isEmpty()){ System.err.println("Modelo é obrigatório."); return; }

        System.out.print("Cor: ");
        String cor = scanner.nextLine().trim();
        if (cor.isEmpty()){ System.err.println("Cor é obrigatória."); return; }

        System.out.print("Tipo (CARRO, MOTO, BICICLETA): ");
        String tipo = scanner.nextLine().trim().toUpperCase();
        if (tipo.isEmpty()){ System.err.println("Tipo é obrigatório."); return; }


        try {
            Veiculo novoVeiculo = veiculoService.cadastrarVeiculo(moradorLogado.getId(), placa, modelo, cor, tipo);
            System.out.println("Veículo cadastrado com sucesso!");
            imprimirDetalhesVeiculo(novoVeiculo);
        } catch (IllegalArgumentException | IllegalStateException e) {
            System.err.println("Erro ao cadastrar veículo: " + e.getMessage());
        } catch (Exception e) {
            log.error("Erro inesperado ao cadastrar veículo para morador {}: {}", moradorLogado.getId(), e.getMessage(), e);
            System.err.println("Ocorreu um erro inesperado ao cadastrar o veículo.");
        }
    }

    private void imprimirDetalhesVeiculo(Veiculo v) {
        System.out.println(String.format("ID: %d | Placa: %s | Modelo: %s | Cor: %s | Tipo: %s",
                v.getId(), v.getPlaca(), v.getModelo(), v.getCor(), v.getTipoVeiculo()
        ));
    }

    private void listarMeusVeiculosPeloMorador(Morador moradorLogado) {
        System.out.println("\n--- Meus Veículos Cadastrados ---");
        try {
            List<Veiculo> veiculos = veiculoService.listarVeiculosPorMorador(moradorLogado.getId());
            if (veiculos.isEmpty()) {
                System.out.println("Você não possui veículos cadastrados.");
            } else {
                veiculos.forEach(this::imprimirDetalhesVeiculo);
            }
        } catch (IllegalArgumentException e) {
            System.err.println("Erro ao listar seus veículos: " + e.getMessage());
        }
    }

    private void removerVeiculoPeloMorador(Scanner scanner, Morador moradorLogado) {
        System.out.println("\n--- Remover Veículo ---");
        listarMeusVeiculosPeloMorador(moradorLogado);

        if (veiculoService.listarVeiculosPorMorador(moradorLogado.getId()).isEmpty()){
            // A mensagem já foi dada por listarMeusVeiculosPeloMorador, mas evitamos pedir ID desnecessariamente
            return;
        }

        System.out.print("Digite o ID do veículo que deseja remover (ou 0 para cancelar): ");
        Long veiculoId = lerLong(scanner);
        if (veiculoId == null || veiculoId == 0) {
            if(veiculoId != null && veiculoId == 0) System.out.println("Remoção cancelada.");
            return;
        }

        System.out.print("Tem certeza que deseja remover este veículo? (S/N): ");
        String confirmacao = scanner.nextLine().trim().toUpperCase();

        if ("S".equals(confirmacao)) {
            try {
                veiculoService.removerVeiculo(veiculoId, moradorLogado.getId());
                System.out.println("Veículo removido com sucesso.");
            } catch (IllegalArgumentException e) {
                System.err.println("Erro ao remover veículo: " + e.getMessage());
            } catch (Exception e) {
                log.error("Erro inesperado ao remover veículo ID {} para morador {}: {}", veiculoId, moradorLogado.getId(), e.getMessage(), e);
                System.err.println("Ocorreu um erro inesperado ao remover o veículo.");
            }
        } else {
            System.out.println("Remoção cancelada pelo usuário.");
        }
    }

    // Seus métodos lerInt e lerLong já ajustados
    private int lerInt(Scanner scanner) {
        try {
            String line = scanner.nextLine().trim();
            if (line.isEmpty()) {
                System.err.println("Entrada inválida. Opção não pode ser vazia. Insira um número inteiro.");
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
            String line = scanner.nextLine().trim();
            if (line.isEmpty()) {
                System.err.println("Entrada inválida. ID não pode ser vazio. Insira um número.");
                return null;
            }
            return Long.parseLong(line);
        } catch (NumberFormatException e) {
            System.err.println("Entrada inválida. Por favor, insira um número (ID).");
            return null;
        }
    }
}