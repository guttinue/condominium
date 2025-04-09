package com.condominium.model;

public class Sindico extends Usuario {
    private String blocoResponsavel;

    public Sindico() {
        super();
    }

    public Sindico(Long id, String nome, String cpf, String email, String telefone, String senha, String blocoResponsavel) {
        super(id, nome, cpf, email, telefone, senha);
        this.blocoResponsavel = blocoResponsavel;
    }

    public String getBlocoResponsavel() {
        return blocoResponsavel;
    }

    public void setBlocoResponsavel(String blocoResponsavel) {
        this.blocoResponsavel = blocoResponsavel;
    }

    @Override
    public String getTipoUsuario() {
        return "Síndico";
    }

    @Override
    public String toString() {
        return "Sindico{" +
                super.toString() +
                ", Bloco Responsável='" + blocoResponsavel + '\'' +
                '}';
    }
}
