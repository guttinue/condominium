package com.condo.service;

import org.springframework.stereotype.Service; // <-- Adicione esta linha
// Seus outros imports para AssembleiaRepository e Autowired viriam aqui
import com.condo.repository.AssembleiaRepository;
import org.springframework.beans.factory.annotation.Autowired;

@Service
public class AssembleiaService {

    private final AssembleiaRepository assembleiaRepository;

    @Autowired
    public AssembleiaService(AssembleiaRepository assembleiaRepository) {
        this.assembleiaRepository = assembleiaRepository;
    }

    // TODO: Implementar métodos para agendar, listar, gerenciar atas, etc.
}