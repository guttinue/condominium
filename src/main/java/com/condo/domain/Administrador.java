package com.condo.domain;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;


@Entity
@DiscriminatorValue("FUNCIONARIO")
public class Administrador extends Usuario {

    @Column(length = 100)
    private String setor;


    public Administrador() {
        super(); // Chama o construtor Usuario()
        addRole(Role.FUNCIONARIO);
    }

    public Administrador(String nome, String email, String senha, String telefone, String cpf,
                         Condominium condominio, String setor) {
        super(nome, email, senha, telefone, cpf, condominio);
        this.setor = setor;
        this.addRole(Role.FUNCIONARIO);
    }

    public String getSetor() {
        return setor;
    }

    public void setSetor(String setor) {
        this.setor = setor;
    }

}