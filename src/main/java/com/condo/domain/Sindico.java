package com.condo.domain;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import java.time.LocalDate;

@Entity
@DiscriminatorValue("SINDICO")
public class Sindico extends Usuario {

    @Column(name = "data_posse")
    private LocalDate dataPosse;


    @Column(name = "bloco_responsavel", length = 100)
    private String blocoResponsavel;


    public Sindico() {
        super();
        addRole(Role.SINDICO);
    }

    public Sindico(String nome, String email, String senha, String telefone, String cpf,
                   Condominium condominio, LocalDate dataPosse, String blocoResponsavel /*, String unidadeSeTambemForMorador */) {
        super(nome, email, senha, telefone, cpf, condominio);
        this.dataPosse = dataPosse;
        this.blocoResponsavel = blocoResponsavel;
        addRole(Role.SINDICO);
    }

    public LocalDate getDataPosse() { return dataPosse; }
    public void setDataPosse(LocalDate dataPosse) { this.dataPosse = dataPosse; }
    public String getBlocoResponsavel() { return blocoResponsavel; }
    public void setBlocoResponsavel(String blocoResponsavel) { this.blocoResponsavel = blocoResponsavel; }
}