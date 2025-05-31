package com.condo.domain;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;


@Entity
@DiscriminatorValue("MORADOR")
public class Morador extends Usuario {

    @Column(length = 100)
    private String unidade;

    public Morador() {
        super();
        addRole(Role.MORADOR);
    }

    public Morador(String nome, String email, String senha, String telefone, String cpf,
                   Condominium condominio, String unidade) {
        super(nome, email, senha, telefone, cpf, condominio);
        this.unidade = unidade;
        this.addRole(Role.MORADOR);
    }

    public String getUnidade() {
        return unidade;
    }

    public void setUnidade(String unidade) {
        this.unidade = unidade;
    }

}