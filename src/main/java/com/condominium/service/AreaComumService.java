package com.condominium.service;

import com.condominium.model.AreaComum;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


public class AreaComumService {
    private final List<AreaComum> areasComuns;

    public AreaComumService() {
        this.areasComuns = new ArrayList<>();
        // Seed de áreas comuns
        areasComuns.add(new AreaComum(1L, "Sala de Festa", "Espaço para festas e eventos"));
        areasComuns.add(new AreaComum(2L, "Churrasqueira", "Área com churrasqueira e espaço gourmet"));
    }

    public List<AreaComum> listarAreas() {
        return List.copyOf(areasComuns);
    }

    public Optional<AreaComum> buscarPorId(Long id) {
        return areasComuns.stream()
                .filter(a -> a.getId().equals(id))
                .findFirst();
    }

    public AreaComum adicionarArea(AreaComum area) {
        areasComuns.add(area);
        return area;
    }

    public AreaComum getSalaFesta() {
        return areasComuns.stream()
                .filter(a -> a.getNome().equals("Sala de Festa"))
                .findFirst()
                .orElseThrow();
    }

    public AreaComum getChurrasqueira() {
        return areasComuns.stream()
                .filter(a -> a.getNome().equals("Churrasqueira"))
                .findFirst()
                .orElseThrow();
    }

    public boolean removerArea(Long id) {
        return areasComuns.removeIf(a -> a.getId().equals(id));
    }
}
