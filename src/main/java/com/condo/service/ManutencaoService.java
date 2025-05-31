package com.condo.service; // Confirme se este é o seu pacote

import com.condo.domain.Morador;
import com.condo.domain.ReportManutencao;
import com.condo.domain.Sindico;
import com.condo.repository.ReportManutencaoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
public class ManutencaoService {

    private final ReportManutencaoRepository reportManutencaoRepository;

    @Autowired
    public ManutencaoService(ReportManutencaoRepository reportManutencaoRepository) {
        this.reportManutencaoRepository = reportManutencaoRepository;
    }

    @Transactional
    public ReportManutencao criarReportManutencao(Morador morador, String local, String descricao) {
        if (morador == null) {
            throw new IllegalArgumentException("Morador não pode ser nulo para criar um relatório.");
        }
        if (local == null || local.trim().isEmpty()) {
            throw new IllegalArgumentException("O local do problema não pode ser vazio.");
        }
        if (descricao == null || descricao.trim().isEmpty()) {
            throw new IllegalArgumentException("A descrição do problema não pode ser vazia.");
        }

        ReportManutencao report = new ReportManutencao();
        report.setSolicitante(morador); // <--- USANDO O SETTER AQUI
        report.setLocal(local);
        report.setDescricao(descricao);
        report.setStatus("ABERTO");
        report.setCriadoEm(LocalDateTime.now());
        report.setAtualizadoEm(LocalDateTime.now()); // <--- USANDO O SETTER AQUI

        return reportManutencaoRepository.save(report);
    }

    public List<ReportManutencao> listarTodosReports() {
        return reportManutencaoRepository.findAll();
    }

    public List<ReportManutencao> listarReportsPorStatus(String status) {
        if (status == null || status.trim().isEmpty()) {
            System.out.println("Status para busca de relatórios não pode ser vazio.");
            return Collections.emptyList();
        }
        return reportManutencaoRepository.findByStatus(status.toUpperCase());
    }

    @Transactional
    public ReportManutencao atualizarStatusManutencao(Long reportId, String novoStatus, String comentario, Sindico sindico) {
        ReportManutencao report = reportManutencaoRepository.findById(reportId)
                .orElseThrow(() -> new IllegalArgumentException("Relatório de manutenção com ID " + reportId + " não encontrado."));

        if (novoStatus == null || novoStatus.trim().isEmpty()) {
            throw new IllegalArgumentException("Novo status não pode ser vazio.");
        }

        report.setStatus(novoStatus.toUpperCase());
        report.setAtualizadoEm(LocalDateTime.now()); // <--- USANDO O SETTER AQUI

        // Lógica para comentário (opcional)
        // if (comentario != null && !comentario.trim().isEmpty()) {
        //     // Adicionar o comentário à entidade, dependendo de como você modelou
        // }

        return reportManutencaoRepository.save(report);
    }

    @Transactional
    public void adicionarComentarioManutencao(Long reportId, String comentario, Sindico sindico) {
        ReportManutencao report = reportManutencaoRepository.findById(reportId)
                .orElseThrow(() -> new IllegalArgumentException("Relatório de manutenção com ID " + reportId + " não encontrado."));

        if (comentario == null || comentario.trim().isEmpty()) {
            throw new IllegalArgumentException("Comentário não pode ser vazio.");
        }

        // Implementar lógica de adicionar comentário aqui, conforme sua entidade ReportManutencao
        System.out.println("Lógica de adicionar comentário para report ID " + reportId + " (Síndico: " + sindico.getNome() + "): " + comentario);

        report.setAtualizadoEm(LocalDateTime.now()); // <--- USANDO O SETTER AQUI
        reportManutencaoRepository.save(report);
    }

    public Optional<ReportManutencao> buscarReportPorId(Long id) {
        return reportManutencaoRepository.findById(id);
    }
}