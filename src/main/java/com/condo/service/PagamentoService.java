package com.condo.service; // Ou seu pacote de serviço

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
import java.time.LocalDateTime;
import java.util.List;

@Service
public class PagamentoService {

    private static final Logger log = LoggerFactory.getLogger(PagamentoService.class);

    private final PagamentoRepository pagamentoRepository;
    private final MoradorRepository moradorRepository; // Para buscar o morador ao gerar taxa
    private final CondominiumRepository condominiumRepository; // Para associar ao condomínio

    @Autowired
    public PagamentoService(PagamentoRepository pagamentoRepository,
                            MoradorRepository moradorRepository,
                            CondominiumRepository condominiumRepository) {
        this.pagamentoRepository = pagamentoRepository;
        this.moradorRepository = moradorRepository;
        this.condominiumRepository = condominiumRepository;
    }

    /**
     * Gera uma nova taxa condominial (Pagamento com status PENDENTE).
     */
    @Transactional
    public Pagamento gerarNovaTaxaCondominial(Long moradorId, BigDecimal valor, LocalDate dataVencimento, String descricao, Long condominioId) {
        Morador morador = moradorRepository.findById(moradorId)
                .orElseThrow(() -> new IllegalArgumentException("Morador com ID " + moradorId + " não encontrado."));

        Condominium condominio = condominiumRepository.findById(condominioId)
                .orElseThrow(() -> new IllegalArgumentException("Condomínio com ID " + condominioId + " não encontrado."));

        if (valor == null || valor.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("O valor da taxa deve ser positivo.");
        }
        if (dataVencimento == null || dataVencimento.isBefore(LocalDate.now().minusDays(1))) { // Permite gerar para hoje
            throw new IllegalArgumentException("Data de vencimento inválida ou no passado.");
        }
        if (descricao == null || descricao.trim().isEmpty()) {
            throw new IllegalArgumentException("A descrição da taxa é obrigatória.");
        }

        Pagamento novaTaxa = new Pagamento(morador, condominio, valor, dataVencimento, descricao);
        log.info("Gerando nova taxa: Morador ID {}, Valor R${}, Vencimento {}, Descrição '{}'",
                moradorId, valor, dataVencimento, descricao);
        return pagamentoRepository.save(novaTaxa);
    }

    /**
     * Registra o pagamento de uma taxa existente.
     * Atualiza o status da taxa para "PAGO" e define a data de pagamento.
     */
    @Transactional
    public Pagamento registrarPagamentoEfetuado(Long taxaId, LocalDate dataPagamento) {
        Pagamento taxa = pagamentoRepository.findById(taxaId)
                .orElseThrow(() -> new IllegalArgumentException("Taxa com ID " + taxaId + " não encontrada."));

        if (!"PENDENTE".equalsIgnoreCase(taxa.getStatus()) && !"ATRASADO".equalsIgnoreCase(taxa.getStatus())) {
            throw new IllegalStateException("Esta taxa não está pendente ou atrasada. Status atual: " + taxa.getStatus());
        }
        if (dataPagamento == null || dataPagamento.isAfter(LocalDate.now())) {
            throw new IllegalArgumentException("Data de pagamento inválida ou no futuro.");
        }

        taxa.setDataPagamento(dataPagamento);
        taxa.setStatus("PAGO");
        taxa.setAtualizadoEm(LocalDateTime.now());
        log.info("Registrando pagamento para Taxa ID {}: Data Pgto {}, Novo Status PAGO", taxaId, dataPagamento);
        Pagamento taxaPaga = pagamentoRepository.save(taxa);

        // Lógica para "atualizar status financeiro do morador para adimplente":
        // Isso é mais complexo. Ser adimplente pode significar não ter NENHUMA taxa PENDENTE ou ATRASADA.
        // Uma abordagem seria:
        // verificarAdimplenciaMorador(taxa.getMorador());
        // Este método `verificarAdimplenciaMorador` consultaria todas as taxas do morador.
        // Por ora, estamos apenas marcando esta taxa específica como PAGA.

        return taxaPaga;
    }

    /**
     * Lista pagamentos pendentes de todos os moradores.
     */
    public List<Pagamento> listarPagamentosPendentes() {
        return pagamentoRepository.findByStatus("PENDENTE");
    }

    /**
     * Lista todos os pagamentos (para fins de consulta, por exemplo).
     */
    public List<Pagamento> listarTodosPagamentos() {
        return pagamentoRepository.findAll();
    }

    // Método auxiliar potencial para verificar e atualizar adimplência do morador
    /*
    @Transactional
    private void verificarAdimplenciaMorador(Morador morador) {
        List<Pagamento> pendencias = pagamentoRepository.findByMoradorAndStatusIn(morador, List.of("PENDENTE", "ATRASADO"));
        if (pendencias.isEmpty()) {
            // Marcar morador como adimplente (ex: morador.setStatusFinanceiro("ADIMPLENTE");)
            // Isso requer um campo 'statusFinanceiro' na entidade Morador.
            // log.info("Morador {} agora está adimplente.", morador.getNome());
        } else {
            // Marcar morador como inadimplente
            // log.info("Morador {} possui {} pendências financeiras.", morador.getNome(), pendencias.size());
        }
        // moradorRepository.save(morador); // Se houver alteração no morador
    }
    */
}