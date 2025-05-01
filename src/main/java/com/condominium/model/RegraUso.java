package com.condominium.model;

public class RegraUso {
    private Long id;
    private String descricao; // Ex.: limite de reservas mensais, horários bloqueados, etc.

    public RegraUso() {}

    public RegraUso(Long id, String descricao) {
        this.id = id;
        this.descricao = descricao;
    }
    // Getters & Setters
    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public String getDescricao() {
        return descricao;
    }
    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    @Override
    public String toString() {
        return "RegraUso{" +
                "id=" + id +
                ", descricao='" + descricao + '\'' +
                '}';
    }
}
