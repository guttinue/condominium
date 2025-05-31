package com.condo.domain;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "pagamentos")
public class Pagamento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "morador_id", nullable = false)
    private Morador morador;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "condominio_id", nullable = false)
    private Condominium condominio;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal valor;

    @Column(name = "data_vencimento", nullable = false)
    private LocalDate dataVencimento;

    @Column(name = "data_pagamento")
    private LocalDate dataPagamento;

    @Column(nullable = false, length = 30)
    private String status;

    @Column(name = "descricao", length = 255)
    private String descricao;

    @Column(name = "criado_em", nullable = false, updatable = false)
    private LocalDateTime criadoEm;

    @Column(name = "atualizado_em")
    private LocalDateTime atualizadoEm;

    public Pagamento() {
    }

    public Pagamento(Morador morador, Condominium condominio, BigDecimal valor, LocalDate dataVencimento, String descricao) {
        this.morador = morador;
        this.condominio = condominio;
        this.valor = valor;
        this.dataVencimento = dataVencimento;
        this.descricao = descricao;
        this.status = "PENDENTE"; // Status inicial
        this.criadoEm = LocalDateTime.now();
        this.atualizadoEm = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Morador getMorador() { return morador; }
    public void setMorador(Morador morador) { this.morador = morador; }
    public Condominium getCondominio() { return condominio; }
    public void setCondominio(Condominium condominio) { this.condominio = condominio; }
    public BigDecimal getValor() { return valor; }
    public void setValor(BigDecimal valor) { this.valor = valor; }
    public LocalDate getDataVencimento() { return dataVencimento; }
    public void setDataVencimento(LocalDate dataVencimento) { this.dataVencimento = dataVencimento; }
    public LocalDate getDataPagamento() { return dataPagamento; }
    public void setDataPagamento(LocalDate dataPagamento) { this.dataPagamento = dataPagamento; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }
    public LocalDateTime getCriadoEm() { return criadoEm; }
    public void setCriadoEm(LocalDateTime criadoEm) { this.criadoEm = criadoEm; }
    public LocalDateTime getAtualizadoEm() { return atualizadoEm; }
    public void setAtualizadoEm(LocalDateTime atualizadoEm) { this.atualizadoEm = atualizadoEm; }

    @Override
    public String toString() {
        return "Pagamento{" +
                "id=" + id +
                ", morador=" + (morador != null ? morador.getNome() : "N/A") +
                ", valor=" + valor +
                ", dataVencimento=" + dataVencimento +
                ", status='" + status + '\'' +
                (dataPagamento != null ? ", dataPagamento=" + dataPagamento : "") +
                '}';
    }
}