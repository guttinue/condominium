package com.condominium.model;

import java.util.HashSet;
import java.util.Set;

public abstract class Usuario {
    private Long id;
    private String nome;
    private String cpf;
    private String email;
    private String telefone;
    private String senha;
    private Set<Role> roles = new HashSet<>();

    public Usuario() {
    }

    public Usuario(Long id, String nome, String cpf, String email, String telefone, String senha) {
        this.id = id;
        this.nome = nome;
        this.cpf = cpf;
        this.email = email;
        this.telefone = telefone;
        this.senha = senha;
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

    public String getCpf() {
        return cpf;
    }

    public void setCpf(String cpf) {
        this.cpf = cpf;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTelefone() {
        return telefone;
    }

    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }

    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }

    public abstract String getTipoUsuario();

    public Set<Role> getRoles() {
        return roles;
    }
    public void addRole(Role r) {
        roles.add(r);
    }

    @Override
    public String toString() {
        return "ID=" + id +
                ", Nome='" + nome + '\'' +
                ", CPF='" + cpf + '\'' +
                ", Email='" + email + '\'' +
                ", Telefone='" + telefone + '\'';
    }
}