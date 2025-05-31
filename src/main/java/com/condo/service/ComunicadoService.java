package com.condo.service;

import com.condo.domain.Comunicado;
import com.condo.domain.Condominium;
import com.condo.domain.Sindico;
import com.condo.domain.Usuario;
import com.condo.repository.ComunicadoRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ComunicadoService {

    private static final Logger log = LoggerFactory.getLogger(ComunicadoService.class);

    private final ComunicadoRepository comunicadoRepository;

    @Autowired
    public ComunicadoService(ComunicadoRepository comunicadoRepository) {
        this.comunicadoRepository = comunicadoRepository;
    }

    @Transactional
    public Comunicado criarComunicado(String titulo, String conteudo, Usuario autor, Condominium condominio) {
        if (!(autor instanceof Sindico)) {
            log.warn("Tentativa de criação de comunicado por usuário que não é síndico. Usuário ID: {}", autor.getId());
            throw new SecurityException("Apenas síndicos podem criar comunicados.");
        }
        if (titulo == null || titulo.trim().isEmpty()) {
            throw new IllegalArgumentException("O título do comunicado não pode ser vazio.");
        }
        if (conteudo == null || conteudo.trim().isEmpty()) {
            throw new IllegalArgumentException("O conteúdo do comunicado não pode ser vazio.");
        }
        if (condominio == null) {
            throw new IllegalArgumentException("O condomínio associado ao comunicado não pode ser nulo.");
        }

        Sindico sindico = (Sindico) autor;

        Comunicado novoComunicado = new Comunicado(titulo.trim(), conteudo.trim(), sindico, condominio);
        Comunicado comunicadoSalvo = comunicadoRepository.save(novoComunicado);
        log.info("Comunicado ID {} criado com sucesso pelo Síndico ID {} para o Condomínio ID {}",
                comunicadoSalvo.getId(), sindico.getId(), condominio.getId());
        return comunicadoSalvo;
    }

    @Transactional(readOnly = true)
    public List<Comunicado> listarComunicadosPorCondominio(Condominium condominio) {
        if (condominio == null) {
            log.warn("Tentativa de listar comunicados para condomínio nulo.");
            throw new IllegalArgumentException("Condomínio não pode ser nulo para listar comunicados.");
        }
        log.info("Listando comunicados para o Condomínio ID: {}", condominio.getId());
        return comunicadoRepository.findByCondominioOrderByDataPublicacaoDesc(condominio);
    }

    @Transactional(readOnly = true)
    public List<Comunicado> listarComunicadosPorSindico(Usuario sindico) {
        if (!(sindico instanceof Sindico)) {
            log.warn("Tentativa de listar comunicados por um usuário que não é síndico. Usuário ID: {}", sindico.getId());
            throw new SecurityException("Apenas síndicos podem listar os comunicados que eles próprios emitiram desta forma.");
        }
        log.info("Listando comunicados emitidos pelo Síndico ID: {}", sindico.getId());
        return comunicadoRepository.findBySindicoOrderByDataPublicacaoDesc(sindico);
    }
}