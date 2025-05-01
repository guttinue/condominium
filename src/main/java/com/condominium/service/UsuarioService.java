package com.condominium.service;

import com.condominium.model.Administrador;
import com.condominium.model.Morador;
import com.condominium.model.Sindico;
import com.condominium.model.Usuario;

import java.util.ArrayList;
import java.util.List;

public class UsuarioService {
    private List<Usuario> usuarios = new ArrayList<>();
    private Long usuarioCounter = 1L;

    public Morador adicionarMorador(String nome, String cpf, String email, String telefone, String senha, String unidade) {
        Morador morador = new Morador(usuarioCounter++, nome, cpf, email, telefone, senha, unidade);
        usuarios.add(morador);
        return morador;
    }

    public Sindico adicionarSindico(String nome, String cpf, String email, String telefone, String senha, String blocoResponsavel) {
        Sindico sindico = new Sindico(usuarioCounter++, nome, cpf, email, telefone, senha, blocoResponsavel);
        usuarios.add(sindico);
        return sindico;
    }

    public Administrador adicionarAdministrador(String nome, String cpf, String email, String telefone, String senha, String setor) {
        Administrador admin = new Administrador(usuarioCounter++, nome, cpf, email, telefone, senha, setor);
        usuarios.add(admin);
        return admin;
    }

    // Método para listar todos os usuários cadastrados
    public List<Usuario> listarUsuarios() {
        return usuarios;
    }
}
