package com.condominium.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class ReportManutencao {
    private Long id;
    private String local;
    private String descricao;
    private StatusManutencao status;
    private boolean externo;
    private String protocolo;
    private List<String> comentarios = new ArrayList<>();
    private List<String> historico = new ArrayList<>();

    public ReportManutencao(Long id, String local, String descricao, boolean externo) {
        this.id = id;
        this.local = local;
        this.descricao = descricao;
        this.externo = externo;
        this.status = StatusManutencao.PENDENTE;
        this.protocolo = "MR-" + id + "-" + System.currentTimeMillis();
        logHistory("Criado com status PENDENTE");
    }

    // Getters (omitidos para brevidade)...
    public Long getId() { return id; }
    public String getLocal() { return local; }
    public String getDescricao() { return descricao; }
    public StatusManutencao getStatus() { return status; }
    public boolean isExterno() { return externo; }
    public String getProtocolo() { return protocolo; }
    public List<String> getComentarios() { return comentarios; }
    public List<String> getHistorico() { return historico; }

    // Operações específicas:
    public void addComentario(String comentario) {
        comentarios.add(comentario);
        logHistory("Comentário adicionado: " + comentario);
    }

    public void updateStatus(StatusManutencao novoStatus) {
        this.status = novoStatus;
        logHistory("Status alterado para " + novoStatus);
    }

    private void logHistory(String entry) {
        historico.add(LocalDateTime.now() + " - " + entry);
    }

    @Override
    public String toString() {
        return String.format("ID=%d | Protocolo=%s | Local=%s | Status=%s | Externo=%s",
                id, protocolo, local, status, externo);
    }
}
