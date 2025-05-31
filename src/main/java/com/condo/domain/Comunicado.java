package com.condo.domain;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "comunicados")
public class Comunicado {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 200)
    private String titulo;

    @Lob
    @Column(nullable = false, columnDefinition = "TEXT")
    private String conteudo;

    @Column(nullable = false)
    private LocalDateTime dataPublicacao;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "sindico_id", nullable = false)
    private Usuario sindico;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "condominio_id", nullable = false)
    private Condominium condominio;

    public Comunicado() {
        this.dataPublicacao = LocalDateTime.now();
    }

    public Comunicado(String titulo, String conteudo, Usuario sindico, Condominium condominio) {
        this.titulo = titulo;
        this.conteudo = conteudo;
        this.sindico = sindico;
        this.condominio = condominio;
        this.dataPublicacao = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getConteudo() {
        return conteudo;
    }

    public void setConteudo(String conteudo) {
        this.conteudo = conteudo;
    }

    public LocalDateTime getDataPublicacao() {
        return dataPublicacao;
    }

    public void setDataPublicacao(LocalDateTime dataPublicacao) {
        this.dataPublicacao = dataPublicacao;
    }

    public Usuario getSindico() {
        return sindico;
    }

    public void setSindico(Usuario sindico) {
        this.sindico = sindico;
    }

    public Condominium getCondominio() {
        return condominio;
    }

    public void setCondominio(Condominium condominio) {
        this.condominio = condominio;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Comunicado that = (Comunicado) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Comunicado{" +
                "id=" + id +
                ", titulo='" + titulo + '\'' +
                ", dataPublicacao=" + dataPublicacao +
                ", sindico=" + (sindico != null ? sindico.getNome() : "N/A") +
                ", condominio=" + (condominio != null ? condominio.getNome() : "N/A") +
                '}';
    }
}