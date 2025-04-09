package com.condominium.model;

public class Dependente {
    private Long id;
    private String nome;
    private String parentesco;

    public Dependente() {}

    public Dependente(Long id, String nome, String parentesco) {
        this.id = id;
        this.nome = nome;
        this.parentesco = parentesco;
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

    public String getParentesco() {
        return parentesco;
    }

    public void setParentesco(String parentesco) {
        this.parentesco = parentesco;
    }

    @Override
    public String toString() {
        return "Dependente{" +
                "id=" + id +
                ", Nome='" + nome + '\'' +
                ", Parentesco='" + parentesco + '\'' +
                '}';
    }
}
