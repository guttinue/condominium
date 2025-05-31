// src/main/java/com/condo/domain/AreaComum.java
package com.condo.domain;

import jakarta.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "area_comum")
public class AreaComum {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true) // Nome da área comum geralmente é único dentro do condomínio
    private String nome;

    @Lob // Para textos mais longos
    @Column(columnDefinition = "TEXT")
    private String regrasUso;

    // RELACIONAMENTO BIDIRECIONAL: Lado "Muitos" da relação ManyToOne com Condominium
    @ManyToOne(fetch = FetchType.LAZY) // LAZY é geralmente preferível aqui
    @JoinColumn(name = "condominium_id", nullable = false) // Define a coluna FK na tabela 'area_comum'
    private Condominium condominio; // Nome do atributo é 'condominio'

    public AreaComum() {
    }

    // Construtor pode ser ajustado para receber Condominium
    public AreaComum(String nome, String regrasUso, Condominium condominio) {
        this.nome = nome;
        this.regrasUso = regrasUso;
        this.condominio = condominio;
    }

    // Getters e Setters
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

    public String getRegrasUso() {
        return regrasUso;
    }

    public void setRegrasUso(String regrasUso) {
        this.regrasUso = regrasUso;
    }

    public Condominium getCondominio() { // Getter para o atributo 'condominio'
        return condominio;
    }

    public void setCondominio(Condominium condominio) {
        this.condominio = condominio;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AreaComum areaComum = (AreaComum) o;
        return Objects.equals(id, areaComum.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "AreaComum{" +
                "id=" + id +
                ", nome='" + nome + '\'' +
                // Evitar chamar getNome() de condominio aqui para não causar LazyInitializationException em logs simples
                // A menos que você saiba que condominio estará sempre inicializado ao chamar toString().
                ", condominioId=" + (condominio != null ? condominio.getId() : "null") +
                '}';
    }
}