package com.condo.domain;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "reports_manutencao")
public class ReportManutencao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "morador_id", nullable = false)
    private Morador solicitante;

    @Column(nullable = false, length = 200)
    private String local;

    @Lob
    @Column(nullable = false, columnDefinition = "TEXT")
    private String descricao;

    @Column(nullable = false, length = 50)
    private String status;

    @Column(name = "criado_em", nullable = false, updatable = false)
    private LocalDateTime criadoEm;

    @Column(name = "atualizado_em")
    private LocalDateTime atualizadoEm;

    public ReportManutencao() {
    }

    public ReportManutencao(Morador solicitante, String local, String descricao, String status) {
        this.solicitante = solicitante;
        this.local = local;
        this.descricao = descricao;
        this.status = status;
        this.criadoEm = LocalDateTime.now();
        this.atualizadoEm = LocalDateTime.now();
    }


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Morador getSolicitante() {
        return solicitante;
    }

    public void setSolicitante(Morador solicitante) { // <--- MÉTODO QUE VOCÊ PEDIU
        this.solicitante = solicitante;
    }

    public String getLocal() {
        return local;
    }

    public void setLocal(String local) {
        this.local = local;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getCriadoEm() {
        return criadoEm;
    }

    public void setCriadoEm(LocalDateTime criadoEm) {
        this.criadoEm = criadoEm;
    }

    public LocalDateTime getAtualizadoEm() {
        return atualizadoEm;
    }

    public void setAtualizadoEm(LocalDateTime atualizadoEm) { // <--- MÉTODO QUE VOCÊ PEDIU
        this.atualizadoEm = atualizadoEm;
    }

    @Override
    public String toString() {
        return "ReportManutencao{" +
                "id=" + id +
                ", solicitante=" + (solicitante != null ? solicitante.getNome() : "N/A") +
                ", local='" + local + '\'' +
                ", status='" + status + '\'' +
                ", criadoEm=" + criadoEm +
                ", atualizadoEm=" + atualizadoEm +
                '}';
    }
}