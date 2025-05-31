// src/main/java/com/condo/menu/FuncionarioMenu.java
package com.condo.menu;

import com.condo.domain.Administrador;
import com.condo.domain.Morador;
import com.condo.domain.Pagamento;
import com.condo.domain.Visitante;
import com.condo.domain.Veiculo;
import com.condo.service.VeiculoService;
import com.condo.service.VisitanteService;
import com.condo.service.PagamentoService;
import com.condo.service.UsuarioService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;

@Component
public class FuncionarioMenu {

    private static final Logger log = LoggerFactory.getLogger(FuncionarioMenu.class);
    private static final DateTimeFormatter DATE_FORMATTER_INPUT = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final DateTimeFormatter DATE_FORMATTER_DISPLAY = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final DateTimeFormatter DATETIME_FORMATTER_DISPLAY = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    private final VisitanteService visitanteService;
    private final UsuarioService usuarioService;
    private final PagamentoService pagamentoService;
    private final VeiculoService veiculoService;

    @Autowired
    public FuncionarioMenu(VisitanteService visitanteService, UsuarioService usuarioService, PagamentoService pagamentoService, VeiculoService veiculoService) {
        this.visitanteService = visitanteService;
        this.usuarioService = usuarioService;
        this.pagamentoService = pagamentoService;
        this.veiculoService = veiculoService;
    }

    public void exibirMenu(Scanner scanner, Administrador funcionarioLogado) {
        boolean continuar = true;
        while (continuar) {
            System.out.println("\n--- Menu do Funcionário (" + funcionarioLogado.getNome() + " | Setor: "
                    + funcionarioLogado.getSetor() + ") ---");
            System.out.println("1. Gerenciar Cadastro de Moradores");
            System.out.println("2. Gerenciar Taxas Condominiais");
            System.out.println("3. Emitir Boleto para Taxa"); // Alterado
            System.out.println("4. Consultar Status de Pagamento (Não implementado)");
            System.out.println("5. Gerenciar Entradas/Saídas de Visitantes");
            System.out.println("6. Consultar Histórico de Visitantes");
            System.out.println("7. Listar veiculos existentes");
            System.out.println("0. Voltar (Logout)");
            System.out.print("Escolha uma opção: ");

            try {
                int opcao = lerInt(scanner);

                switch (opcao) {
                    case 1:
                        gerenciarCadastroMoradores(scanner, funcionarioLogado);
                        break;
                    case 2:
                        gerenciarTaxasCondominiais(scanner, funcionarioLogado);
                        break;
                    case 3:
                        emitirBoletoParaTaxa(scanner); // Novo método chamado
                        break;
                    case 4:
                        System.out.println("Funcionalidade 'Consultar Status de Pagamento' ainda não implementada.");
                        break;
                    case 5:
                        gerenciarEntradaSaidaVisitantes(scanner, funcionarioLogado);
                        break;
                    case 6:
                        consultarHistoricoVisitantesPeloFuncionario(scanner, funcionarioLogado);
                        break;
                    case 7:
                        listarVeiculosDoCondominioPeloFuncionario(funcionarioLogado);
                        break;
                    case 0:
                        continuar = false;
                        System.out.println("Fazendo logout...");
                        break;
                    default:
                        System.out.println("Opção inválida. Tente novamente.");
                }
            } catch (Exception e) {
                log.error("Erro no menu do funcionário: {}", e.getMessage(), e);
                System.out.println("Ocorreu um erro inesperado: " + e.getMessage() + ". Tente novamente.");
            }
        }
    }

    private void gerenciarCadastroMoradores(Scanner scanner, Administrador funcionarioLogado) {
        boolean subMenuContinuar = true;
        while (subMenuContinuar) {
            System.out.println("\n--- Gerenciar Cadastro de Moradores ---");
            System.out.println("1. Cadastrar Novo Morador");
            System.out.println("2. Listar Todos os Moradores");
            System.out.println("3. Buscar Morador");
            System.out.println("4. Editar Morador");
            System.out.println("5. Excluir Morador");
            System.out.println("0. Voltar ao Menu Principal do Funcionário");
            System.out.print("Escolha uma opção: ");

            int opcaoCadastro = lerInt(scanner);

            switch (opcaoCadastro) {
                case 1:
                    cadastrarNovoMorador(scanner);
                    break;
                case 2:
                    listarTodosMoradores();
                    break;
                case 3:
                    buscarMorador(scanner);
                    break;
                case 4:
                    editarMorador(scanner);
                    break;
                case 5:
                    excluirMorador(scanner);
                    break;
                case 0:
                    subMenuContinuar = false;
                    break;
                default:
                    System.out.println("Opção inválida.");
            }
        }
    }

    private void cadastrarNovoMorador(Scanner scanner) {
        System.out.println("\n--- Cadastrar Novo Morador ---");
        System.out.print("Nome completo: ");
        String nome = scanner.nextLine();
        System.out.print("Email: ");
        String email = scanner.nextLine();
        System.out.print("Telefone (ex: (XX)XXXXX-XXXX): ");
        String telefone = scanner.nextLine();
        System.out.print("CPF (ex: XXX.XXX.XXX-XX): ");
        String cpf = scanner.nextLine();
        System.out.print("Unidade (ex: Apt 101, Casa 2B): ");
        String unidade = scanner.nextLine();
        System.out.print("Senha para o novo morador: ");
        String senha = scanner.nextLine();

        if (nome.trim().isEmpty() || email.trim().isEmpty() ||
                cpf.trim().isEmpty() || unidade.trim().isEmpty() || senha.trim().isEmpty()) {
            System.out.println("Nome, email, CPF, unidade e senha são obrigatórios.");
            return;
        }

        try {
            Morador novoMorador = usuarioService.cadastrarNovoMorador(nome, email, senha, telefone, cpf, unidade);
            System.out.println("Morador cadastrado com sucesso!");
            imprimirDetalhesMorador(novoMorador);
        } catch (IllegalArgumentException e) {
            System.out.println("Erro ao cadastrar morador: " + e.getMessage());
        } catch (Exception e) {
            log.error("Erro inesperado ao cadastrar morador: {}", e.getMessage(), e);
            System.out.println("Ocorreu um erro inesperado durante o cadastro.");
        }
    }

    private void imprimirDetalhesMorador(Morador morador) {
        if (morador == null) {
            System.out.println("Dados do morador não disponíveis.");
            return;
        }
        System.out.println("ID: " + morador.getId() + " | Nome: " + morador.getNome() +
                " | Email: " + morador.getEmail() +
                " | Telefone: " + (morador.getTelefone() != null && !morador.getTelefone().isBlank() ? morador.getTelefone() : "N/A") +
                " | CPF: " + (morador.getCpf() != null && !morador.getCpf().isBlank() ? morador.getCpf() : "N/A") +
                " | Unidade: " + morador.getUnidade());
    }

    private void listarTodosMoradores() {
        System.out.println("\n--- Lista de Moradores Cadastrados ---");
        List<Morador> moradores = usuarioService.listarTodosMoradores();
        if (moradores.isEmpty()) {
            System.out.println("Nenhum morador cadastrado.");
        } else {
            moradores.forEach(this::imprimirDetalhesMorador);
        }
    }

    private void buscarMorador(Scanner scanner) {
        System.out.println("\n--- Buscar Morador ---");
        System.out.print("Digite o ID, CPF, Email ou a Unidade do morador: ");
        String identificador = scanner.nextLine();

        try {
            Long id = Long.parseLong(identificador);
            Optional<Morador> moradorOpt = usuarioService.buscarMoradorPorId(id);
            if (moradorOpt.isPresent()) {
                System.out.println("Morador encontrado por ID:");
                imprimirDetalhesMorador(moradorOpt.get());
                return;
            }
        } catch (NumberFormatException e) {
            // Silenciosamente ignora se não for um número, para tentar outros identificadores
        } catch (IllegalArgumentException e) {
            System.out.println(e.getMessage()); // Se buscarMoradorPorId lançar erro de ID inválido
            return;
        }


        Optional<Morador> moradorOpt = usuarioService.buscarMoradorPorIdentificador(identificador);
        if (moradorOpt.isPresent()) {
            System.out.println("Morador encontrado:");
            imprimirDetalhesMorador(moradorOpt.get());
        } else {
            System.out.println("Nenhum morador encontrado com o identificador: " + identificador);
        }
    }

    private void editarMorador(Scanner scanner) {
        System.out.println("\n--- Editar Cadastro de Morador ---");
        listarTodosMoradores();
        System.out.print("Digite o ID do morador que deseja editar (ou 0 para cancelar): ");
        Long moradorId = lerLong(scanner);

        if (moradorId == null || moradorId == 0) {
            if (moradorId != null && moradorId == 0) System.out.println("Edição cancelada.");
            return;
        }

        Optional<Morador> moradorOpt = usuarioService.buscarMoradorPorId(moradorId);
        if (moradorOpt.isEmpty()) {
            System.out.println("Morador com ID " + moradorId + " não encontrado.");
            return;
        }

        Morador morador = moradorOpt.get();
        System.out.println("\nEditando morador:");
        imprimirDetalhesMorador(morador);

        System.out.print("Novo nome (atual: '" + morador.getNome() + "', deixe em branco para não alterar): ");
        String novoNome = scanner.nextLine();

        System.out.print("Novo email (atual: '" + morador.getEmail() + "', deixe em branco para não alterar): ");
        String novoEmail = scanner.nextLine();

        System.out.print("Novo telefone (atual: '" + (morador.getTelefone() != null ? morador.getTelefone() : "") + "', deixe em branco para não alterar): ");
        String novoTelefone = scanner.nextLine();

        System.out.print("Nova unidade (atual: '" + morador.getUnidade() + "', deixe em branco para não alterar): ");
        String novaUnidade = scanner.nextLine();

        try {
            Morador moradorAtualizado = usuarioService.atualizarMorador(moradorId,
                    novoNome.isBlank() ? null : novoNome, // Passa null se em branco, serviço decidirá se mantém ou não
                    novoEmail.isBlank() ? null : novoEmail,
                    novoTelefone.isBlank() ? null : novoTelefone,
                    novaUnidade.isBlank() ? null : novaUnidade);
            System.out.println("Morador atualizado com sucesso!");
            imprimirDetalhesMorador(moradorAtualizado);
        } catch (IllegalArgumentException e) {
            System.out.println("Erro ao atualizar morador: " + e.getMessage());
        } catch (Exception e) {
            log.error("Erro inesperado ao atualizar morador ID {}: {}", moradorId, e.getMessage(), e);
            System.out.println("Ocorreu um erro inesperado durante a atualização.");
        }
    }

    private void excluirMorador(Scanner scanner) {
        System.out.println("\n--- Excluir Morador ---");
        listarTodosMoradores();
        System.out.print("Digite o ID do morador que deseja excluir (ou 0 para cancelar): ");
        Long moradorId = lerLong(scanner);

        if (moradorId == null || moradorId == 0) {
            if (moradorId != null && moradorId == 0) System.out.println("Exclusão cancelada.");
            return;
        }

        Optional<Morador> moradorOpt = usuarioService.buscarMoradorPorId(moradorId);
        if (moradorOpt.isEmpty()) {
            System.out.println("Morador com ID " + moradorId + " não encontrado.");
            return;
        }

        Morador moradorParaExcluir = moradorOpt.get();
        System.out.println("\nDados do morador a ser excluído:");
        imprimirDetalhesMorador(moradorParaExcluir);
        System.out.print("Tem certeza que deseja EXCLUIR este morador? Esta ação não pode ser desfeita. (S/N): ");
        String confirmacao = scanner.nextLine().trim().toUpperCase();

        if ("S".equals(confirmacao)) {
            try {
                usuarioService.excluirMorador(moradorId);
                System.out.println("Morador excluído com sucesso.");
            } catch (IllegalArgumentException | IllegalStateException e) {
                System.out.println("Erro ao excluir morador: " + e.getMessage());
            } catch (Exception e) {
                log.error("Erro inesperado ao excluir morador ID {}: {}", moradorId, e.getMessage(), e);
                System.out.println("Ocorreu um erro inesperado durante a exclusão: " + e.getMessage());
            }
        } else {
            System.out.println("Exclusão cancelada pelo usuário.");
        }
    }

    private void gerenciarTaxasCondominiais(Scanner scanner, Administrador funcionarioLogado) {
        boolean subMenuContinuar = true;
        while(subMenuContinuar) {
            System.out.println("\n--- Gerenciar Taxas Condominiais ---");
            System.out.println("1. Gerar Nova Taxa para Morador");
            System.out.println("2. Registrar Pagamento Efetuado de Taxa");
            System.out.println("3. Listar Taxas Pendentes (Geral)"); // Alterado
            System.out.println("4. Listar Todas as Taxas (Geral)");   // Alterado
            System.out.println("0. Voltar ao Menu do Funcionário");
            System.out.print("Escolha uma opção: ");
            int escolha = lerInt(scanner);

            switch (escolha) {
                case 1:
                    gerarNovaTaxa(scanner, funcionarioLogado);
                    break;
                case 2:
                    registrarPagamentoEfetuado(scanner);
                    break;
                case 3:
                    listarTaxasPendentesGeral(); // Alterado para chamar o método correto
                    break;
                case 4:
                    listarTodasAsTaxasGeral(); // Alterado para chamar o método correto
                    break;
                case 0:
                    subMenuContinuar = false;
                    break;
                default:
                    System.out.println("Opção inválida.");
            }
        }
    }

    private void gerarNovaTaxa(Scanner scanner, Administrador funcionarioLogado) {
        System.out.println("\n--- Gerar Nova Taxa Condominial ---");
        listarTodosMoradores();
        System.out.print("Digite o ID do Morador para gerar a taxa (ou 0 para cancelar): ");
        Long moradorId = lerLong(scanner);
        if (moradorId == null || moradorId == 0) {
            if (moradorId != null && moradorId == 0) System.out.println("Operação cancelada.");
            return;
        }

        Optional<Morador> moradorOpt = usuarioService.buscarMoradorPorId(moradorId);
        if(moradorOpt.isEmpty()){
            System.out.println("Morador com ID " + moradorId + " não encontrado.");
            return;
        }
        System.out.println("Gerando taxa para: " + moradorOpt.get().getNome());

        System.out.print("Valor da taxa (ex: 350.75): ");
        BigDecimal valor;
        try {
            String valorStr = scanner.nextLine();
            if (valorStr.isBlank()) throw new NumberFormatException("Valor não pode ser em branco.");
            valor = new BigDecimal(valorStr.replace(",", ".")); // Aceita vírgula como separador decimal
        } catch (NumberFormatException e) {
            System.out.println("Valor inválido. Use números e opcionalmente '.' ou ',' como separador decimal.");
            return;
        }

        System.out.print("Data de vencimento (dd/MM/yyyy): ");
        LocalDate dataVencimento;
        try {
            String dataVencStr = scanner.nextLine();
            if (dataVencStr.isBlank()) throw new DateTimeParseException("Data não pode ser em branco.", dataVencStr, 0);
            dataVencimento = LocalDate.parse(dataVencStr, DATE_FORMATTER_INPUT);
        } catch (DateTimeParseException e) {
            System.out.println("Formato de data inválido. Use dd/MM/yyyy.");
            return;
        }

        System.out.print("Descrição da taxa (ex: Condomínio Maio/2025): ");
        String descricao = scanner.nextLine();
        if (descricao.isBlank()){
            System.out.println("Descrição da taxa é obrigatória.");
            return;
        }

        try {
            if (funcionarioLogado.getCondominio() == null || funcionarioLogado.getCondominio().getId() == null) {
                System.out.println("ERRO: Funcionário não está associado a um condomínio válido. Não é possível gerar taxa.");
                log.warn("Tentativa de gerar taxa por funcionário {} sem condomínio associado ou ID do condomínio nulo.", funcionarioLogado.getEmail());
                return;
            }
            Long condominioId = funcionarioLogado.getCondominio().getId();

            Pagamento novaTaxa = pagamentoService.gerarNovaTaxaCondominial(moradorId, valor, dataVencimento, descricao, condominioId);
            System.out.println("Nova taxa ID " + novaTaxa.getId() + " gerada com sucesso para " + moradorOpt.get().getNome() + ".");
            imprimirDetalhesPagamento(novaTaxa);
        } catch (IllegalArgumentException e) {
            System.out.println("Erro ao gerar taxa: " + e.getMessage());
        } catch (Exception e) {
            log.error("Erro inesperado ao gerar taxa para morador ID {}: {}", moradorId, e.getMessage(), e);
            System.out.println("Ocorreu um erro inesperado ao gerar a taxa.");
        }
    }

    private void registrarPagamentoEfetuado(Scanner scanner) {
        System.out.println("\n--- Registrar Pagamento Efetuado ---");
        listarTaxasPendentesGeral(); // Lista todas as pendentes para facilitar a escolha

        System.out.print("Digite o ID da Taxa que foi paga (ou 0 para cancelar): ");
        Long taxaId = lerLong(scanner);
        if (taxaId == null || taxaId == 0) {
            if (taxaId != null && taxaId == 0) System.out.println("Operação cancelada.");
            return;
        }

        System.out.print("Data do pagamento (dd/MM/yyyy, deixe em branco para usar hoje): ");
        LocalDate dataPagamento;
        try {
            String dataPagStr = scanner.nextLine();
            if (dataPagStr.isBlank()) {
                dataPagamento = LocalDate.now(); // Usa data atual se em branco
                System.out.println("Usando data atual para pagamento: " + dataPagamento.format(DATE_FORMATTER_DISPLAY));
            } else {
                dataPagamento = LocalDate.parse(dataPagStr, DATE_FORMATTER_INPUT);
            }
        } catch (DateTimeParseException e) {
            System.out.println("Formato de data inválido. Use dd/MM/yyyy.");
            return;
        }

        try {
            Pagamento taxaPaga = pagamentoService.registrarPagamentoEfetuado(taxaId, dataPagamento);
            System.out.println("Pagamento da taxa ID " + taxaPaga.getId() + " registrado com sucesso!");
            imprimirDetalhesPagamento(taxaPaga);
        } catch (IllegalArgumentException | IllegalStateException e) {
            System.out.println("Erro ao registrar pagamento: " + e.getMessage());
        } catch (Exception e) {
            log.error("Erro inesperado ao registrar pagamento para taxa ID {}: {}", taxaId, e.getMessage(), e);
            System.out.println("Ocorreu um erro inesperado ao registrar o pagamento.");
        }
    }

    private void imprimirDetalhesPagamento(Pagamento p) {
        Morador morador = p.getMorador();
        String nomeMorador = (morador != null) ? morador.getNome() : "N/A";
        String unidadeMorador = (morador != null && morador.getUnidade() != null) ? morador.getUnidade() : "N/A";

        System.out.println(
                String.format("ID Pg: %-4d | Morador: %-25.25s (Un: %-8.8s) | Desc: %-30.30s | Valor: R$ %-8.2f | Venc: %-10s | Status: %-15s %s",
                        p.getId(),
                        nomeMorador,
                        unidadeMorador,
                        p.getDescricao(),
                        p.getValor(),
                        p.getDataVencimento().format(DATE_FORMATTER_DISPLAY),
                        p.getStatus(),
                        (p.getDataPagamento() != null ? "| Pago em: " + p.getDataPagamento().format(DATE_FORMATTER_DISPLAY) : "")
                )
        );
    }

    // Método chamado pelo menu quando o funcionário quer ver taxas pendentes gerais
    private void listarTaxasPendentesGeral() {
        System.out.println("\n--- Taxas Condominiais Pendentes (Geral) ---");
        // Chama o método correto no PagamentoService que lista TODAS as pendentes
        List<Pagamento> pendentes = pagamentoService.listarTodosPagamentosPorStatus("PENDENTE");
        if (pendentes.isEmpty()) {
            System.out.println("Nenhuma taxa pendente encontrada no condomínio.");
        } else {
            pendentes.forEach(this::imprimirDetalhesPagamento);
        }
    }

    private void listarTodasAsTaxasGeral() {
        System.out.println("\n--- Todas as Taxas Condominiais (Geral) ---");
        List<Pagamento> todas = pagamentoService.listarTodosOsPagamentos();
        if (todas.isEmpty()) {
            System.out.println("Nenhuma taxa encontrada no sistema.");
        } else {
            todas.forEach(this::imprimirDetalhesPagamento);
        }
    }

    // NOVO MÉTODO PARA EMITIR BOLETO
    private void emitirBoletoParaTaxa(Scanner scanner) {
        System.out.println("\n--- Emitir Boleto para Taxa ---");

        System.out.println("Taxas com pagamento pendente ou em atraso:");
        List<Pagamento> pagamentosParaBoleto = pagamentoService.listarTodosPagamentosPorStatus("PENDENTE");
        pagamentosParaBoleto.addAll(pagamentoService.listarTodosPagamentosPorStatus("ATRASADO")); // Adiciona os atrasados também

        if (pagamentosParaBoleto.isEmpty()) {
            System.out.println("Nenhuma taxa pendente ou em atraso encontrada para emissão de boleto.");
            return;
        }
        pagamentosParaBoleto.forEach(this::imprimirDetalhesPagamento);

        System.out.print("\nDigite o ID da Taxa para emitir o boleto (ou 0 para cancelar): ");
        Long pagamentoId = lerLong(scanner);
        if (pagamentoId == null || pagamentoId == 0) {
            if (pagamentoId != null && pagamentoId == 0) System.out.println("Emissão de boleto cancelada.");
            return;
        }

        try {
            String dadosBoleto = pagamentoService.gerarDadosBoleto(pagamentoId);
            System.out.println("\n--- BOLETO GERADO (SIMULAÇÃO) ---");
            System.out.println(dadosBoleto);
            System.out.println("--- FIM DO BOLETO ---");
        } catch (IllegalArgumentException | IllegalStateException e) {
            System.err.println("Erro ao emitir boleto: " + e.getMessage());
        } catch (Exception e) {
            log.error("Erro inesperado ao emitir boleto para pagamento ID {}: {}", pagamentoId, e.getMessage(), e);
            System.err.println("Ocorreu um erro inesperado ao emitir o boleto.");
        }
    }


    private void imprimirDetalhesCompletosVisitante(Visitante v) {
        Morador moradorResponsavel = v.getMoradorResponsavel();
        String nomeMorador = (moradorResponsavel != null) ? moradorResponsavel.getNome() : "N/A";

        System.out.println(String.format(
                "ID: %d | Nome: %s | Doc: %s | Placa: %s | Previsto: %s | Unid: %s | Status: %s | Morador Resp.: %s",
                v.getId(),
                v.getNomeVisitante(),
                v.getDocumentoVisitante() != null ? v.getDocumentoVisitante() : "N/A",
                v.getPlacaVeiculo() != null ? v.getPlacaVeiculo() : "N/A",
                v.getDataHoraVisitaPrevista().format(DATETIME_FORMATTER_DISPLAY),
                v.getUnidadeDestino(),
                v.getStatusVisita(),
                nomeMorador
        ));
        if (v.getDataHoraEntradaEfetiva() != null) {
            System.out.println("  Entrada Efetiva: " + v.getDataHoraEntradaEfetiva().format(DATETIME_FORMATTER_DISPLAY));
        }
        if (v.getDataHoraSaidaEfetiva() != null) {
            System.out.println("  Saída Efetiva: " + v.getDataHoraSaidaEfetiva().format(DATETIME_FORMATTER_DISPLAY));
        }
    }

    private void gerenciarEntradaSaidaVisitantes(Scanner scanner, Administrador funcionarioLogado) {
        boolean subMenuContinuar = true;
        while(subMenuContinuar) {
            System.out.println("\n--- Gerenciar Entradas/Saídas de Visitantes ---");
            System.out.println("1. Listar Visitantes Esperados Hoje");
            System.out.println("2. Registrar Entrada de Visitante");
            System.out.println("3. Registrar Saída de Visitante");
            System.out.println("0. Voltar");
            System.out.print("Escolha uma opção: ");
            int escolha = lerInt(scanner);

            try {
                if (funcionarioLogado.getCondominio() == null || funcionarioLogado.getCondominio().getId() == null) {
                    System.out.println("ERRO: Funcionário não está associado a um condomínio válido.");
                    return;
                }
                Long condominioId = funcionarioLogado.getCondominio().getId();

                switch(escolha) {
                    case 1:
                        listarVisitantesEsperadosHojePeloFuncionario(condominioId);
                        break;
                    case 2:
                        registrarEntradaVisitantePeloFuncionario(scanner, condominioId);
                        break;
                    case 3:
                        registrarSaidaVisitantePeloFuncionario(scanner, condominioId);
                        break;
                    case 0:
                        subMenuContinuar = false;
                        break;
                    default:
                        System.out.println("Opção inválida.");
                }
            } catch (IllegalArgumentException | IllegalStateException e) {
                System.out.println("Erro: " + e.getMessage());
            } catch (Exception e) {
                log.error("Erro inesperado no gerenciamento de visitantes: {}", e.getMessage(), e);
                System.out.println("Ocorreu um erro inesperado.");
            }
        }
    }

    private void listarVisitantesEsperadosHojePeloFuncionario(Long condominioId) {
        System.out.println("\n--- Visitantes Esperados Hoje (" + LocalDate.now().format(DATE_FORMATTER_DISPLAY) + ") ---");
        List<Visitante> visitantes = visitanteService.listarVisitantesEsperadosHoje(condominioId);
        if (visitantes.isEmpty()) {
            System.out.println("Nenhum visitante esperado para hoje.");
        } else {
            visitantes.forEach(this::imprimirDetalhesCompletosVisitante);
        }
    }

    private void registrarEntradaVisitantePeloFuncionario(Scanner scanner, Long condominioId) {
        System.out.println("\n--- Registrar Entrada de Visitante ---");
        listarVisitantesEsperadosHojePeloFuncionario(condominioId);
        System.out.print("Digite o ID do Visitante que está entrando (ou 0 para cancelar): ");
        Long visitanteId = lerLong(scanner);
        if (visitanteId == null || visitanteId == 0) return;

        try {
            Visitante visitante = visitanteService.registrarEntradaVisitante(visitanteId);
            System.out.println("Entrada registrada com sucesso para: " + visitante.getNomeVisitante());
            imprimirDetalhesCompletosVisitante(visitante);
        } catch (IllegalArgumentException | IllegalStateException e) {
            System.out.println("Erro ao registrar entrada: " + e.getMessage());
        }
    }

    private void registrarSaidaVisitantePeloFuncionario(Scanner scanner, Long condominioId) {
        System.out.println("\n--- Registrar Saída de Visitante ---");

        System.out.println("Visitantes atualmente DENTRO do condomínio (Status 'CHEGOU'):");
        List<Visitante> visitantesPresentes = visitanteService.listarVisitantesPresentes(condominioId);
        if (visitantesPresentes.isEmpty()) {
            System.out.println("Nenhum visitante com status 'CHEGOU' encontrado para registrar saída.");
            return;
        } else {
            visitantesPresentes.forEach(this::imprimirDetalhesCompletosVisitante);
        }

        System.out.print("\nDigite o ID do Visitante que está saindo (ou 0 para cancelar): ");
        Long visitanteId = lerLong(scanner);
        if (visitanteId == null || visitanteId == 0) {
            if (visitanteId != null && visitanteId == 0) System.out.println("Operação cancelada.");
            return;
        }

        try {
            Visitante visitante = visitanteService.registrarSaidaVisitante(visitanteId);
            System.out.println("Saída registrada com sucesso para: " + visitante.getNomeVisitante());
            imprimirDetalhesCompletosVisitante(visitante);
        } catch (IllegalArgumentException | IllegalStateException e) {
            System.out.println("Erro ao registrar saída: " + e.getMessage());
        } catch (Exception e) {
            log.error("Erro inesperado ao registrar saída do visitante ID {}: {}", visitanteId, e.getMessage(), e);
            System.out.println("Ocorreu um erro inesperado.");
        }
    }

    private void consultarHistoricoVisitantesPeloFuncionario(Scanner scanner, Administrador funcionarioLogado) {
        System.out.println("\n--- Histórico de Visitantes ---");
        System.out.print("Filtrar por nome do visitante (deixe em branco para não filtrar): ");
        String nomeFiltro = scanner.nextLine();
        System.out.print("Filtrar por data da visita (dd/MM/yyyy, deixe em branco para não filtrar): ");
        String dataStr = scanner.nextLine();
        System.out.print("Filtrar por unidade de destino (deixe em branco para não filtrar): ");
        String unidadeFiltro = scanner.nextLine();

        Optional<LocalDate> dataFiltro = Optional.empty();
        if (!dataStr.isBlank()) {
            try {
                dataFiltro = Optional.of(LocalDate.parse(dataStr, DATE_FORMATTER_INPUT));
            } catch (DateTimeParseException e) {
                System.out.println("Formato de data para filtro inválido. Ignorando filtro por data.");
            }
        }

        try {
            if (funcionarioLogado.getCondominio() == null || funcionarioLogado.getCondominio().getId() == null) {
                System.out.println("ERRO: Funcionário não está associado a um condomínio válido.");
                return;
            }
            Long condominioId = funcionarioLogado.getCondominio().getId();

            List<Visitante> historico = visitanteService.consultarHistoricoVisitantes(
                    condominioId,
                    nomeFiltro.isBlank() ? Optional.empty() : Optional.of(nomeFiltro),
                    dataFiltro,
                    unidadeFiltro.isBlank() ? Optional.empty() : Optional.of(unidadeFiltro)
            );

            if (historico.isEmpty()) {
                System.out.println("Nenhum registro encontrado para os filtros aplicados.");
            } else {
                System.out.println("Resultados encontrados:");
                historico.forEach(this::imprimirDetalhesCompletosVisitante);
            }
        } catch (IllegalArgumentException e){
            System.out.println("Erro ao consultar histórico: " + e.getMessage());
        }
        catch (Exception e) {
            log.error("Erro inesperado ao consultar histórico de visitantes: {}", e.getMessage(), e);
            System.out.println("Ocorreu um erro inesperado.");
        }
    }

    private void imprimirDetalhesVeiculoComUnidade(Veiculo v) {
        Morador morador = v.getMorador();
        String unidadeMorador = (morador != null && morador.getUnidade() != null) ? morador.getUnidade() : "N/A";
        String nomeMorador = (morador != null) ? morador.getNome() : "N/A";

        System.out.println(String.format("Placa: %s | Modelo: %s | Cor: %s | Tipo: %s | Unidade Morador: %s (Nome: %s)",
                v.getPlaca(), v.getModelo(), v.getCor(), v.getTipoVeiculo(),
                unidadeMorador,
                nomeMorador
        ));
    }

    private void listarVeiculosDoCondominioPeloFuncionario(Administrador funcionarioLogado) {
        System.out.println("\n--- Veículos Cadastrados no Condomínio ---");
        try {
            if (funcionarioLogado.getCondominio() == null || funcionarioLogado.getCondominio().getId() == null) {
                System.out.println("ERRO: Funcionário não está associado a um condomínio válido.");
                return;
            }
            Long condominioId = funcionarioLogado.getCondominio().getId();
            List<Veiculo> veiculos = veiculoService.listarTodosVeiculosPorCondominio(condominioId);
            if (veiculos.isEmpty()) {
                System.out.println("Nenhum veículo cadastrado no condomínio.");
            } else {
                veiculos.forEach(this::imprimirDetalhesVeiculoComUnidade);
            }
        } catch (IllegalArgumentException e) {
            System.out.println("Erro: " + e.getMessage());
        } catch (Exception e) {
            log.error("Erro inesperado ao listar veículos do condomínio: {}", e.getMessage(), e);
            System.out.println("Ocorreu um erro inesperado.");
        }
    }

    private int lerInt(Scanner scanner) {
        try {
            String line = scanner.nextLine();
            if (line.isBlank()) {
                System.out.println("Entrada inválida. Por favor, insira um número inteiro.");
                return -99;
            }
            return Integer.parseInt(line);
        } catch (NumberFormatException e) {
            System.out.println("Entrada inválida. Por favor, insira um número inteiro.");
            return -99;
        }
    }

    private Long lerLong(Scanner scanner) {
        try {
            String line = scanner.nextLine();
            if (line.isBlank()) {
                System.out.println("Entrada inválida. Por favor, insira um número.");
                return null;
            }
            return Long.parseLong(line);
        } catch (NumberFormatException e) {
            System.out.println("Entrada inválida. Por favor, insira um número (long).");
            return null;
        }
    }
}