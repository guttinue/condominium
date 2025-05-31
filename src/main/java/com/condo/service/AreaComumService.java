package com.condo.service;

import org.springframework.stereotype.Service; // <-- Adicione esta linha
import java.util.List; // <-- Adicione esta linha se ainda não estiver lá
import java.util.Optional;
// Seus outros imports para AreaComum, AreaComumRepository, Autowired viriam aqui
import com.condo.domain.AreaComum;
import com.condo.repository.AreaComumRepository;
import org.springframework.beans.factory.annotation.Autowired;


@Service
public class AreaComumService {

    private final AreaComumRepository areaComumRepository;

    @Autowired
    public AreaComumService(AreaComumRepository areaComumRepository) {
        this.areaComumRepository = areaComumRepository;
    }

    public List<AreaComum> listarTodas() {
        return areaComumRepository.findAll();
    }

    public Optional<AreaComum> buscarPorId(Long id) {
        return areaComumRepository.findById(id);
    }

    public Optional<AreaComum> buscarPorNome(String nome) {
        return areaComumRepository.findByNome(nome);
    }
    // TODO: Implementar métodos para CRUD de áreas comuns (para síndico/admin)
}