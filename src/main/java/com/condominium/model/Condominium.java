package com.condominium.model;

import java.util.ArrayList;
import java.util.List;

public class Condominium {
    private Long id;
    private String nome;
    private List<AreaComum> areasComuns = new ArrayList<>();

    private List<Morador> moradores = new ArrayList<>();
    private List<Sindico> sindicos = new ArrayList<>();
    private List<Administrador> administradores = new ArrayList<>();

    public Condominium() {
    }

    public Condominium(Long id, String nome) {
        this.id = id;
        this.nome = nome;
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

    public List<Morador> getMoradores() {
        return moradores;
    }

    public List<Sindico> getSindicos() {
        return sindicos;
    }

    public List<Administrador> getAdministradores() {
        return administradores;
    }

    public void addMorador(Morador m) {
        moradores.add(m);
    }

    public void addSindico(Sindico s) {
        sindicos.add(s);
    }

    public void addAdministrador(Administrador a) {
        administradores.add(a);
    }

    public void addAreaComum(AreaComum a) { areasComuns.add(a); }

    public List<AreaComum> getAreasComuns() { return List.copyOf(areasComuns); }

    @Override
    public String toString() {
        return "Condominium{" +
                "id=" + id +
                ", nome='" + nome + '\'' +
                ", moradores=" + moradores +
                ", sindicos=" + sindicos +
                ", administradores=" + administradores +
                '}';
    }
}
