// src/main/java/com/condo/domain/Condominium.java
package com.condo.domain;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "condominium")
public class Condominium {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String nome;

    @Column(length = 500)
    private String endereco;

    // RELACIONAMENTO BIDIRECIONAL: Lado "Um" da relação OneToMany com AreaComum
    // 'mappedBy = "condominio"' indica que o campo 'condominio' na entidade AreaComum gerencia a chave estrangeira.
    @OneToMany(mappedBy = "condominio", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<AreaComum> areas = new ArrayList<>();

    // Relacionamento com Usuario (Sindico responsável) - Se você quiser manter este relacionamento
    // Opcional: Pode ser apenas um campo com o ID ou nome do síndico se não precisar do objeto completo.
    // Se for um objeto, certifique-se que a entidade Sindico/Usuario também tenha o mapeamento correto.
    // @OneToOne // Ou @ManyToOne se um Sindico puder gerenciar múltiplos condomínios (improvável)
    // @JoinColumn(name = "sindico_responsavel_id", referencedColumnName = "id")
    // private Sindico sindicoResponsavel;


    public Condominium() {
    }

    public Condominium(String nome, String endereco) {
        this.nome = nome;
        this.endereco = endereco;
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
        // Para manter a consistência do lado bidirecional ao setar uma nova lista
        if (areas != null) {
            for (AreaComum area : areas) {
                area.setCondominio(this);
            }
        }
    }

    // Métodos auxiliares para gerenciar o relacionamento bidirecional
    public void addAreaComum(AreaComum area) {
        if (area != null) {
            this.areas.add(area);
            area.setCondominio(this);
        }
    }

    public void removeAreaComum(AreaComum area) {
        if (area != null) {
            this.areas.remove(area);
            area.setCondominio(null);
        }
    }

    // public Sindico getSindicoResponsavel() { return sindicoResponsavel; }
    // public void setSindicoResponsavel(Sindico sindicoResponsavel) { this.sindicoResponsavel = sindicoResponsavel; }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Condominium that = (Condominium) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Condominium{" +
                "id=" + id +
                ", nome='" + nome + '\'' +
                '}';
    }
}