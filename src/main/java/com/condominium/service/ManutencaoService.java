package com.condominium.service;

import com.condominium.model.ReportManutencao;
import com.condominium.model.StatusManutencao;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ManutencaoService {
    private List<ReportManutencao> reports = new ArrayList<>();
    private Long counter = 1L;

    // morador
    public ReportManutencao reportarProblema(String local, String descricao, boolean externo) {
        ReportManutencao mr = new ReportManutencao(counter++, local, descricao, externo);
        reports.add(mr);
        return mr;
    }

    // manutenção
    public List<ReportManutencao> listarTodos() {
        return reports;
    }

    public Optional<ReportManutencao> findById(Long id) {
        return reports.stream().filter(r -> r.getId().equals(id)).findFirst();
    }

    public boolean adicionarComentario(Long id, String comentario) {
        Optional<ReportManutencao> opt = findById(id);
        opt.ifPresent(r -> r.addComentario(comentario));
        return opt.isPresent();
    }

    public boolean alterarStatus(Long id, StatusManutencao status) {
        Optional<ReportManutencao> opt = findById(id);
        opt.ifPresent(r -> r.updateStatus(status));
        return opt.isPresent();
    }
}
