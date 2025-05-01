package com.condominium.model;

public class Administrador extends Usuario {
    private String setor;

    public Administrador() {
        super();
    }

    public Administrador(Long id, String nome, String cpf, String email, String telefone, String senha, String setor) {
        super(id, nome, cpf, email, telefone, senha);
        this.setor = setor;
    }

    public String getSetor() {
        return setor;
    }

    public void setSetor(String setor) {
        this.setor = setor;
    }

    @Override
    public String getTipoUsuario() {
        return "Administrador";
    }

    @Override
    public String toString() {
        return "Administrador{" +
                super.toString() +
                ", Setor='" + setor + '\'' +
                '}';
    }
}
