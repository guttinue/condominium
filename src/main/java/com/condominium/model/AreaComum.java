package com.condominium.model;

import java.util.ArrayList;
import java.util.List;

public class AreaComum {
    private Long id;
    private String nome;
    private String descricao;
    private List<RegraUso> regrasUso = new ArrayList<>();

    public AreaComum() {}

    public AreaComum(Long id, String nome, String descricao) {
        this.id = id;
        this.nome = nome;
        this.descricao = descricao;
    }

    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public String getNome() {
        return nome;
    }
    public void setNome(String nome) {
        this.nome = nome;
    }
    public String getDescricao() {
        return descricao;
    }
    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }
    public List<RegraUso> getRegrasUso() {
        return regrasUso;
    }
    public void setRegrasUso(List<RegraUso> regrasUso) {
        this.regrasUso = regrasUso;
    }
    public void addRegraUso(RegraUso regra) {
        regrasUso.add(regra);
    }

    @Override
    public String toString() {
        return "AreaComum{" +
                "id=" + id +
                ", nome='" + nome + '\'' +
                ", descricao='" + descricao + '\'' +
                '}';
    }
}
