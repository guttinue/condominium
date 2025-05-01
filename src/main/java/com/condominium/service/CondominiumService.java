package com.condominium.service;

import com.condominium.model.Administrador;
import com.condominium.model.Condominium;
import com.condominium.model.Morador;
import com.condominium.model.Sindico;

public class CondominiumService {
    private Condominium condominium;
    private Long usuarioCounter = 1L; // Para atribuir IDs aos usuários

    public CondominiumService(String nomeCondominium) {
        this.condominium = new Condominium(usuarioCounter, nomeCondominium);
    }

    public Condominium getCondominium() {
        return condominium;
    }

    public Morador adicionarMorador(String nome, String cpf, String email, String telefone, String senha, String unidade) {
        Morador morador = new Morador(usuarioCounter++, nome, cpf, email, telefone, senha, unidade);
        condominium.addMorador(morador);
        return morador;
    }

    public Sindico adicionarSindico(String nome, String cpf, String email, String telefone, String senha, String blocoResponsavel) {
        Sindico sindico = new Sindico(usuarioCounter++, nome, cpf, email, telefone, senha, blocoResponsavel);
        condominium.addSindico(sindico);
        return sindico;
    }

    public Administrador adicionarAdministrador(String nome, String cpf, String email, String telefone, String senha, String setor) {
        Administrador admin = new Administrador(usuarioCounter++, nome, cpf, email, telefone, senha, setor);
        condominium.addAdministrador(admin);
        return admin;
    }
}
