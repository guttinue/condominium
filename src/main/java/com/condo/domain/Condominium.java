package com.condo.domain;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "condominium")
public class Condominium {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nome;
    private String endereco;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "condominium_id")
    private List<AreaComum> areas = new ArrayList<>();

    public Condominium() {
    }

    public Condominium(String nome, String endereco) {
        this.nome = nome;
        this.endereco = endereco;
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

    public String getEndereco() {
        return endereco;
    }

    public void setEndereco(String endereco) {
        this.endereco = endereco;
    }

    public List<AreaComum> getAreas() {
        return areas;
    }

    public void setAreas(List<AreaComum> areas) {
        this.areas = areas;
    }

    public void addAreaComum(AreaComum area) {
        this.areas.add(area);

    }
}