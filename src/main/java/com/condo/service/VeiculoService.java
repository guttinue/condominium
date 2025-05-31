package com.condo.service;

import com.condo.domain.Condominium;
import com.condo.domain.Morador;
import com.condo.domain.Veiculo;
import com.condo.repository.CondominiumRepository;
import com.condo.repository.MoradorRepository;
import com.condo.repository.VeiculoRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class VeiculoService {

    private static final Logger log = LoggerFactory.getLogger(VeiculoService.class);

    private final VeiculoRepository veiculoRepository;
    private final MoradorRepository moradorRepository;
    private final CondominiumRepository condominiumRepository; // Para obter o objeto Condominium

    @Autowired
    public VeiculoService(VeiculoRepository veiculoRepository,
                          MoradorRepository moradorRepository,
                          CondominiumRepository condominiumRepository) {
        this.veiculoRepository = veiculoRepository;
        this.moradorRepository = moradorRepository;
        this.condominiumRepository = condominiumRepository;
    }

    @Transactional
    public Veiculo cadastrarVeiculo(Long moradorId, String placa, String modelo, String cor, String tipoVeiculo) {
        Morador morador = moradorRepository.findById(moradorId)
                .orElseThrow(() -> new IllegalArgumentException("Morador com ID " + moradorId + " não encontrado."));

        Condominium condominio = morador.getCondominio();
        if (condominio == null) {
            throw new IllegalStateException("Morador não está associado a um condomínio. Não é possível cadastrar veículo.");
        }

        String placaTratada = placa.trim().toUpperCase();

        if (veiculoRepository.existsByPlacaAndCondominio(placaTratada, condominio)) {
            throw new IllegalArgumentException("Veículo com placa '" + placaTratada + "' já cadastrado neste condomínio.");
        }
        if (modelo.trim().isEmpty() || cor.trim().isEmpty() || tipoVeiculo.trim().isEmpty()) {
            throw new IllegalArgumentException("Modelo, cor e tipo do veículo são obrigatórios.");
        }

        Veiculo novoVeiculo = new Veiculo(placaTratada, modelo.trim(), cor.trim(), tipoVeiculo.trim(), morador, condominio);
        log.info("Cadastrando veículo: Placa {}, Modelo {}, Morador {}", placaTratada, modelo, morador.getNome());
        return veiculoRepository.save(novoVeiculo);
    }

    public List<Veiculo> listarVeiculosPorMorador(Long moradorId) {
        Morador morador = moradorRepository.findById(moradorId)
                .orElseThrow(() -> new IllegalArgumentException("Morador com ID " + moradorId + " não encontrado."));
        log.info("Listando veículos para o morador: {}", morador.getNome());
        return veiculoRepository.findByMoradorOrderByModeloAsc(morador);
    }

    @Transactional
    public void removerVeiculo(Long veiculoId, Long moradorId) {
        Morador morador = moradorRepository.findById(moradorId)
                .orElseThrow(() -> new IllegalArgumentException("Morador com ID " + moradorId + " não encontrado."));

        Veiculo veiculo = veiculoRepository.findByIdAndMorador(veiculoId, morador) // Garante que o morador é o dono
                .orElseThrow(() -> new IllegalArgumentException("Veículo com ID " + veiculoId + " não encontrado ou não pertence a este morador."));

        log.info("Removendo veículo: ID {}, Placa {}, Morador {}", veiculo.getId(), veiculo.getPlaca(), morador.getNome());
        veiculoRepository.delete(veiculo);
    }

    /**
     * Lista todos os veículos de um determinado condomínio.
     * Usado pelo Funcionário.
     */
    public List<Veiculo> listarTodosVeiculosPorCondominio(Long condominioId) {
        Condominium condominio = condominiumRepository.findById(condominioId)
                .orElseThrow(() -> new IllegalArgumentException("Condomínio com ID " + condominioId + " não encontrado."));
        log.info("Listando todos os veículos para o condomínio: {}", condominio.getNome());
        return veiculoRepository.findByCondominioOrderByMorador_UnidadeAscPlacaAsc(condominio);
    }
}