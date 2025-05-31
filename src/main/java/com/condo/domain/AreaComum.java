package com.condo.domain;

import jakarta.persistence.*;

@Entity
@Table(name = "area_comum")
public class AreaComum {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nome;
    private String regrasUso;

    public AreaComum() {
    }

    public AreaComum(String nome, String regrasUso) {
        this.nome = nome;
        this.regrasUso = regrasUso;
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

    public String getRegrasUso() {
        return regrasUso;
    }

    public void setRegrasUso(String regrasUso) {
        this.regrasUso = regrasUso;
    }
}