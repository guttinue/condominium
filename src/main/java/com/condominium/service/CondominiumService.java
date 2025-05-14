package com.condominium.service;

import com.condominium.model.Administrador;
import com.condominium.model.Condominium;
import com.condominium.model.Morador;
import com.condominium.model.Role;
import com.condominium.model.Sindico;

public class CondominiumService {
    private final Condominium condominium;
    private Long usuarioCounter = 1L; // sequência de IDs

    public CondominiumService(String nomeCondominium) {
        this.condominium = new Condominium(usuarioCounter, nomeCondominium);
    }

    public Condominium getCondominium() {
        return condominium;
    }

    public Morador adicionarMorador(
            String nome,
            String cpf,
            String email,
            String telefone,
            String senha,
            String unidade
    ) {
        Morador m = new Morador(usuarioCounter++, nome, cpf, email, telefone, senha, unidade);
        m.addRole(Role.MORADOR);
        condominium.addMorador(m);
        return m;
    }

    public Sindico adicionarSindico(
            String nome,
            String cpf,
            String email,
            String telefone,
            String senha,
            String blocoResponsavel
    ) {
        Sindico s = new Sindico(usuarioCounter++, nome, cpf, email, telefone, senha, blocoResponsavel);
        s.addRole(Role.SINDICO);
        condominium.addSindico(s);
        return s;
    }

    public Administrador adicionarAdministrador(
            String nome,
            String cpf,
            String email,
            String telefone,
            String senha,
            String setor
    ) {
        Administrador a = new Administrador(usuarioCounter++, nome, cpf, email, telefone, senha, setor);
        a.addRole(Role.FUNCIONARIO);
        condominium.addAdministrador(a);
        return a;
    }
}
