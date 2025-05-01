package com.condominium.model;

import java.util.ArrayList;
import java.util.List;

public class Morador extends Usuario {
    private String unidade;
    private List<Dependente> dependentes = new ArrayList<>();

    public Morador() {
        super();
    }

    public Morador(Long id, String nome, String cpf, String email, String telefone, String senha, String unidade) {
        super(id, nome, cpf, email, telefone, senha);
        this.unidade = unidade;
    }

    public String getUnidade() {
        return unidade;
    }

    public void setUnidade(String unidade) {
        this.unidade = unidade;
    }

    public List<Dependente> getDependentes() {
        return dependentes;
    }

    public void addDependente(Dependente d) {
        dependentes.add(d);
    }

    @Override
    public String getTipoUsuario() {
        return "Morador";
    }

    @Override
    public String toString() {
        return "Morador{" +
                super.toString() +
                ", Unidade='" + unidade + '\'' +
                ", Dependentes=" + dependentes +
                '}';
    }
}
