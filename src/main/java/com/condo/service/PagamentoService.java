// src/main/java/com/condo/service/PagamentoService.java
package com.condo.service;

import com.condo.domain.Condominium;
import com.condo.domain.Morador;
import com.condo.domain.Pagamento;
import com.condo.repository.CondominiumRepository;
import com.condo.repository.MoradorRepository;
import com.condo.repository.PagamentoRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Random;

@Service
public class PagamentoService {

    private static final Logger log = LoggerFactory.getLogger(PagamentoService.class);

    private final PagamentoRepository pagamentoRepository;
    private final MoradorRepository moradorRepository;
    private final CondominiumRepository condominiumRepository;

    private static final String STATUS_PENDENTE = "PENDENTE";
    private static final DateTimeFormatter DATE_FORMATTER_BOLETO = DateTimeFormatter.ofPattern("dd/MM/yyyy");


    @Autowired
    public PagamentoService(PagamentoRepository pagamentoRepository,
                            MoradorRepository moradorRepository,
                            CondominiumRepository condominiumRepository) {
        this.pagamentoRepository = pagamentoRepository;
        this.moradorRepository = moradorRepository;
        this.condominiumRepository = condominiumRepository;
    }

    @Transactional(readOnly = true)
    public List<Pagamento> listarTaxasPendentesPorMorador(Morador morador) {
        if (morador == null) {
            log.warn("Tentativa de listar taxas pendentes para morador nulo.");
            throw new IllegalArgumentException("Morador não pode ser nulo para listar taxas pendentes.");
        }
        log.info("Buscando taxas pendentes para o morador ID: {}", morador.getId());
        List<Pagamento> taxasPendentes = pagamentoRepository.findByMoradorAndStatusIgnoreCaseOrderByDataVencimentoAsc(morador, STATUS_PENDENTE);
        log.info("Encontradas {} taxas pendentes para o morador ID: {}", taxasPendentes.size(), morador.getId());
        return taxasPendentes;
    }

    @Transactional
    public Pagamento gerarNovaTaxaCondominial(Long moradorId, BigDecimal valor, LocalDate dataVencimento, String descricao, Long condominioId) {
        log.info("Tentativa de gerar nova taxa: Morador ID={}, Valor={}, Vencimento={}, Descricao='{}', Condominio ID={}",
                moradorId, valor, dataVencimento, descricao, condominioId);

        if (moradorId == null || condominioId == null || valor == null || dataVencimento == null || descricao == null || descricao.trim().isEmpty()) {
            throw new IllegalArgumentException("Todos os parâmetros (moradorId, condominioId, valor, dataVencimento, descricao) são obrigatórios para gerar uma nova taxa.");
        }
        if (valor.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("O valor da taxa deve ser positivo.");
        }

        Morador morador = moradorRepository.findById(moradorId)
                .orElseThrow(() -> {
                    log.error("Morador não encontrado com ID: {}", moradorId);
                    return new IllegalArgumentException("Morador não encontrado com ID: " + moradorId);
                });

        Condominium condominio = condominiumRepository.findById(condominioId)
                .orElseThrow(() -> {
                    log.error("Condomínio não encontrado com ID: {}", condominioId);
                    return new IllegalArgumentException("Condomínio não encontrado com ID: " + condominioId);
                });

        Pagamento novoPagamento = new Pagamento(morador, condominio, valor, dataVencimento, descricao.trim());

        try {
            Pagamento pagamentoSalvo = pagamentoRepository.save(novoPagamento);
            log.info("Nova taxa gerada e salva com sucesso. Pagamento ID: {}, Morador: {}, Valor: {}",
                    pagamentoSalvo.getId(), morador.getNome(), valor);
            return pagamentoSalvo;
        } catch (Exception e) {
            log.error("Erro ao salvar a nova taxa para o morador ID {}: {}", moradorId, e.getMessage(), e);
            throw new RuntimeException("Não foi possível salvar a nova taxa. Verifique os logs.", e);
        }
    }

    @Transactional(readOnly = true)
    public List<Pagamento> listarTodosPagamentosPorStatus(String status) {
        if (status == null || status.trim().isEmpty()) {
            log.warn("Tentativa de listar pagamentos com status vazio. Retornando lista vazia.");
            return Collections.emptyList();
        }
        log.info("Buscando todos os pagamentos com status: {}", status.toUpperCase());
        List<Pagamento> pagamentos = pagamentoRepository.findByStatusIgnoreCaseOrderByDataVencimentoAsc(status.toUpperCase());
        log.info("Encontrados {} pagamentos com status: {}", pagamentos.size(), status.toUpperCase());
        return pagamentos;
    }

    // MÉTODO QUE ESTÁ FALTANDO OU COM NOME DIFERENTE NO SEU CÓDIGO:
    @Transactional(readOnly = true)
    public List<Pagamento> listarTodosOsPagamentos() {
        log.info("Buscando todos os pagamentos do sistema.");
        List<Pagamento> todosPagamentos = pagamentoRepository.findAll(); //findAll() é um método padrão do JpaRepository
        log.info("Encontrados {} pagamentos no total.", todosPagamentos.size());
        return todosPagamentos;
    }

    @Transactional(readOnly = true)
    public Optional<Pagamento> buscarPagamentoPorId(Long pagamentoId) {
        return pagamentoRepository.findById(pagamentoId);
    }

    @Transactional
    public Pagamento registrarPagamentoEfetuado(Long pagamentoId, LocalDate dataPagamentoReal) {
        Pagamento pagamento = pagamentoRepository.findById(pagamentoId)
                .orElseThrow(() -> new IllegalArgumentException("Pagamento com ID " + pagamentoId + " não encontrado."));

        if (!STATUS_PENDENTE.equalsIgnoreCase(pagamento.getStatus()) && !"ATRASADO".equalsIgnoreCase(pagamento.getStatus())) {
            throw new IllegalStateException("Este pagamento já foi processado ou está com status '" + pagamento.getStatus() + "'. Apenas pagamentos PENDENTES ou ATRASADOS podem ser registrados.");
        }

        pagamento.setDataPagamento(dataPagamentoReal != null ? dataPagamentoReal : LocalDate.now());
        if (pagamento.getDataPagamento().isAfter(pagamento.getDataVencimento())) {
            pagamento.setStatus("PAGO_COM_ATRASO");
        } else {
            pagamento.setStatus("PAGO");
        }
        pagamento.setAtualizadoEm(java.time.LocalDateTime.now());
        log.info("Pagamento ID {} registrado como {} em {}", pagamentoId, pagamento.getStatus(), pagamento.getDataPagamento());
        return pagamentoRepository.save(pagamento);
    }

    @Transactional(readOnly = true)
    public String gerarDadosBoleto(Long pagamentoId) {
        Pagamento pagamento = pagamentoRepository.findById(pagamentoId)
                .orElseThrow(() -> new IllegalArgumentException("Pagamento com ID " + pagamentoId + " não encontrado para gerar boleto."));

        if (!"PENDENTE".equalsIgnoreCase(pagamento.getStatus()) && !"ATRASADO".equalsIgnoreCase(pagamento.getStatus())) {
            throw new IllegalStateException("Boleto só pode ser gerado para pagamentos com status PENDENTE ou ATRASADO. Status atual: " + pagamento.getStatus());
        }

        Morador morador = pagamento.getMorador();
        Condominium condominio = pagamento.getCondominio();

        Random random = new Random();
        String codigoDeBarras = String.format("858%05d%010d%010d%010d%05d",
                condominio.getId(),
                morador.getId(),
                pagamento.getId(),
                Long.parseLong(pagamento.getDataVencimento().format(DateTimeFormatter.ofPattern("yyyyMMdd"))),
                random.nextInt(99999)
        );
        String linhaDigitavel = codigoDeBarras.replaceAll("(\\d{5})(\\d{5})(\\d{5})(\\d{6})(\\d{5})(\\d{6})(\\d{1})(\\d{14})",
                "$1.$2 $3.$4 $5.$6 $7 $8");

        StringBuilder sb = new StringBuilder();
        sb.append("\n--- DADOS DO BOLETO ---\n");
        sb.append("============================================================\n");
        sb.append(String.format("Beneficiário: %s\n", condominio.getNome()));
        sb.append(String.format("Endereço do Condomínio: %s\n", condominio.getEndereco()));
        sb.append("------------------------------------------------------------\n");
        sb.append(String.format("Pagador: %s\n", morador.getNome()));
        sb.append(String.format("CPF: %s | Unidade: %s\n", morador.getCpf(), morador.getUnidade()));
        sb.append("------------------------------------------------------------\n");
        sb.append(String.format("Nosso Número/ID Pagamento: %d\n", pagamento.getId()));
        sb.append(String.format("Descrição: %s\n", pagamento.getDescricao()));
        sb.append(String.format("Data de Vencimento: %s\n", pagamento.getDataVencimento().format(DATE_FORMATTER_BOLETO)));
        sb.append(String.format("Valor do Documento: R$ %.2f\n", pagamento.getValor()));
        sb.append("------------------------------------------------------------\n");
        sb.append("Instruções (geradas automaticamente):\n");
        sb.append("- Em caso de atraso, verificar encargos com a administração.\n");
        sb.append("- Pagável em qualquer agência bancária ou internet banking até o vencimento.\n");
        sb.append("------------------------------------------------------------\n");
        sb.append("Linha Digitável (Simulada):\n");
        sb.append(linhaDigitavel + "\n");
        sb.append("Código de Barras (Simulado):\n");
        sb.append("||| ").append(codigoDeBarras.substring(0,10)).append(" ")
                .append(codigoDeBarras.substring(10,20)).append(" ")
                .append(codigoDeBarras.substring(20,30)).append(" ")
                .append(codigoDeBarras.substring(30,40)).append(" ")
                .append(codigoDeBarras.substring(40)).append(" |||\n");
        sb.append("============================================================\n");

        log.info("Dados do boleto gerados para Pagamento ID: {}", pagamentoId);
        return sb.toString();
    }
}