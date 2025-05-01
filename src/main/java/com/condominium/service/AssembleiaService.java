package com.condominium.service;

import com.condominium.model.Assembleia;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class AssembleiaService {
    private List<Assembleia> assembleias = new ArrayList<>();
    private Long counter = 1L;

    public Assembleia agendarAssembleia(LocalDate data, LocalTime horario, String local, String pauta, int participantes) {
        Assembleia a = new Assembleia(counter++, data, horario, local, pauta, participantes);
        assembleias.add(a);
        return a;
    }

    public List<Assembleia> listarAssembleias() {
        return assembleias;
    }

    public Optional<Assembleia> buscarPorId(Long id) {
        return assembleias.stream().filter(a -> a.getId().equals(id)).findFirst();
    }

    public boolean atualizarAssembleia(Long id, LocalDate novaData, LocalTime novoHorario, String novaPauta) {
        Optional<Assembleia> opt = buscarPorId(id);
        if (opt.isPresent()) {
            Assembleia a = opt.get();
            a.setData(novaData);
            a.setHorario(novoHorario);
            a.setPauta(novaPauta);
            return true;
        }
        return false;
    }
}
