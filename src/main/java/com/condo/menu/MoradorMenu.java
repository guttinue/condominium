package com.condo.menu;

import com.condo.domain.AreaComum;
import com.condo.domain.Morador;
import com.condo.domain.Reserva;
import com.condo.domain.ReportManutencao;
import com.condo.domain.Visitante;
import com.condo.domain.Veiculo;
import com.condo.service.VeiculoService;
import com.condo.service.VisitanteService;
import com.condo.service.ManutencaoService;
import com.condo.service.ReservaService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;
import java.util.InputMismatchException;

@Component
public class  MoradorMenu {

    private static final Logger log = LoggerFactory.getLogger(MoradorMenu.class);
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");

    private final ReservaService reservaService;
    private final ManutencaoService manutencaoService;
    private final VisitanteService visitanteService;
    private final VeiculoService veiculoService;
    private static final DateTimeFormatter DATETIME_FORMATTER_INPUT = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    @Autowired
    public MoradorMenu(ReservaService reservaService, ManutencaoService manutencaoService, VisitanteService visitanteService, VeiculoService veiculoService) {
        this.reservaService = reservaService;
        this.manutencaoService = manutencaoService;
        this.visitanteService = visitanteService;
        this.veiculoService = veiculoService;
    }

    public void exibirMenu(Scanner scanner, Morador moradorLogado) {
        boolean continuar = true;
        while (continuar) {
            System.out.println("\n--- Menu do Morador (" + moradorLogado.getNome() + " - Unidade: "
                    + moradorLogado.getUnidade() + ") ---");
            System.out.println("1. Ver Áreas Comuns Disponíveis");
            System.out.println("2. Fazer Reserva de Área Comum");
            System.out.println("3. Minhas Reservas");
            System.out.println("4. Cancelar Reserva");
            System.out.println("5. Gerenciar Visitantes");
            System.out.println("6. Reportar Problema de Manutenção");
            System.out.println("7. Meus veiculos");
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
                        gerenciarVisitantes(scanner, moradorLogado);
                        break;
                    case 6:
                        reportarManutencao(scanner, moradorLogado);
                        break;
                    case 7:
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
                log.error("Erro no menu do morador: {}", e.getMessage(), e);
                System.out.println("Ocorreu um erro inesperado: " + e.getMessage() + ". Tente novamente.");
            }
        }
    }

    private void verAreasComuns() {
        System.out.println("\n--- Áreas Comuns ---");
        List<AreaComum> areas = reservaService.listarAreasComuns();
        if (areas.isEmpty()) {
            System.out.println("Nenhuma área comum cadastrada no momento.");
        } else {
            areas.forEach(area -> System.out.println(
                    "ID: " + area.getId() + " | Nome: " + area.getNome() +
                            " | Regras: "
                            + (area.getRegrasUso() != null && !area.getRegrasUso().isEmpty() ? area.getRegrasUso()
                            : "Nenhuma regra específica.")));
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
            if (areaId != null && areaId == 0)
                System.out.println("Operação cancelada.");
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
            if ("cancelar".equalsIgnoreCase(dataStr)) {
                System.out.println("Operação cancelada.");
                return;
            }
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
            if ("cancelar".equalsIgnoreCase(horaInicioStr)) {
                System.out.println("Operação cancelada.");
                return;
            }
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
            if ("cancelar".equalsIgnoreCase(horaFimStr)) {
                System.out.println("Operação cancelada.");
                return;
            }
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
            System.out.println("Solicitação de reserva enviada com sucesso! ID da Reserva: " + novaReserva.getId()
                    + ", Status: " + novaReserva.getStatus());
            System.out.println("Área: " + novaReserva.getAreaComum().getNome() + ", Data: "
                    + novaReserva.getData().format(DATE_FORMATTER) +
                    " das " + novaReserva.getHoraInicio().format(TIME_FORMATTER) + " às "
                    + novaReserva.getHoraFim().format(TIME_FORMATTER));
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
                            " | Horário: " + r.getHoraInicio().format(TIME_FORMATTER) + " - "
                            + r.getHoraFim().format(TIME_FORMATTER) +
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
            if (reservaId != null && reservaId == 0)
                System.out.println("Operação cancelada.");
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
            System.out.println("Local: " + report.getLocal() + ", Descrição: " + report.getDescricao() + ", Status: "
                    + report.getStatus());
        } catch (IllegalArgumentException e) {
            System.out.println("Erro ao reportar problema: " + e.getMessage());
        } catch (Exception e) {
            log.error("Erro inesperado ao reportar manutenção: {}", e.getMessage(), e);
            System.out.println("Ocorreu um erro inesperado ao reportar o problema.");
        }
    }

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
        String nomeVisitante = scanner.nextLine();

        System.out.print("Data e Hora PREVISTA da visita (dd/MM/yyyy HH:mm): ");
        LocalDateTime dataHoraVisita;
        try {
            dataHoraVisita = LocalDateTime.parse(scanner.nextLine(), DATETIME_FORMATTER_INPUT);
        } catch (DateTimeParseException e) {
            System.out.println("Formato de data/hora inválido. Use dd/MM/yyyy HH:mm.");
            return;
        }


        String unidadeDestino = moradorLogado.getUnidade();
        System.out.println("Unidade de destino: " + unidadeDestino);

        System.out.print("Documento do visitante (RG/CPF - opcional, deixe em branco se não tiver): ");
        String documento = scanner.nextLine();
        System.out.print("Placa do veículo (opcional, deixe em branco se não tiver): ");
        String placa = scanner.nextLine();

        try {
            if (moradorLogado.getCondominio() == null) {
                System.out.println("ERRO: Você não está associado a um condomínio. Não é possível registrar visitante.");
                return;
            }
            Long condominioId = moradorLogado.getCondominio().getId();

            Visitante novoVisitante = visitanteService.registrarVisitante(
                    moradorLogado.getId(), nomeVisitante, dataHoraVisita, unidadeDestino, documento, placa, condominioId);
            System.out.println("Visitante '" + novoVisitante.getNomeVisitante() + "' registrado com sucesso para " +
                    dataHoraVisita.format(DATETIME_FORMATTER_INPUT) + "!");
            System.out.println(novoVisitante);
        } catch (IllegalArgumentException e) {
            System.out.println("Erro ao registrar visitante: " + e.getMessage());
        } catch (Exception e) {
            log.error("Erro inesperado ao registrar visitante: {}", e.getMessage(), e);
            System.out.println("Ocorreu um erro inesperado.");
        }
    }

    private void imprimirDetalhesBasicosVisitante(Visitante v) {
        System.out.println(String.format("ID: %d | Nome: %s | Previsto para: %s | Unidade: %s | Status: %s",
                v.getId(), v.getNomeVisitante(),
                v.getDataHoraVisitaPrevista().format(DATETIME_FORMATTER_INPUT),
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
            System.out.println("Erro: " + e.getMessage());
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
            System.out.println("Erro: " + e.getMessage());
        }
    }

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
        String placa = scanner.nextLine().toUpperCase();
        System.out.print("Modelo (ex: Honda Civic): ");
        String modelo = scanner.nextLine();
        System.out.print("Cor: ");
        String cor = scanner.nextLine();
        System.out.print("Tipo (CARRO, MOTO, BICICLETA): ");
        String tipo = scanner.nextLine().toUpperCase();

        try {
            Veiculo novoVeiculo = veiculoService.cadastrarVeiculo(moradorLogado.getId(), placa, modelo, cor, tipo);
            System.out.println("Veículo cadastrado com sucesso!");
            System.out.println(novoVeiculo);
        } catch (IllegalArgumentException | IllegalStateException e) {
            System.out.println("Erro ao cadastrar veículo: " + e.getMessage());
        } catch (Exception e) {
            log.error("Erro inesperado ao cadastrar veículo para morador {}: {}", moradorLogado.getId(), e.getMessage(), e);
            System.out.println("Ocorreu um erro inesperado.");
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
            System.out.println("Erro: " + e.getMessage());
        }
    }

    private void removerVeiculoPeloMorador(Scanner scanner, Morador moradorLogado) {
        System.out.println("\n--- Remover Veículo ---");
        listarMeusVeiculosPeloMorador(moradorLogado);

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
                System.out.println("Erro ao remover veículo: " + e.getMessage());
            } catch (Exception e) {
                log.error("Erro inesperado ao remover veículo ID {} para morador {}: {}", veiculoId, moradorLogado.getId(), e.getMessage(), e);
                System.out.println("Ocorreu um erro inesperado.");
            }
        } else {
            System.out.println("Remoção cancelada pelo usuário.");
        }
    }

    private int lerInt(Scanner scanner) {
        try {
            int valor = scanner.nextInt();
            scanner.nextLine();
            return valor;
        } catch (InputMismatchException e) {
            System.out.println("Entrada inválida. Por favor, insira um número inteiro.");
            scanner.nextLine();
            return -1;
        }
    }

    private Long lerLong(Scanner scanner) {
        try {
            long valor = scanner.nextLong();
            scanner.nextLine();
            return valor;
        } catch (InputMismatchException e) {
            System.out.println("Entrada inválida. Por favor, insira um número.");
            scanner.nextLine();
            return null;
        }
    }
}