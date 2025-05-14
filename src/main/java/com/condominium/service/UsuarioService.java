package com.condominium.service;

import com.condominium.model.Usuario;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class UsuarioService {
    private final List<Usuario> usuarios = new ArrayList<>();

    public void registerAll(List<Usuario> mor, List<Usuario> sin, List<Usuario> adm) {
        usuarios.clear();
        usuarios.addAll(mor);
        usuarios.addAll(sin);
        usuarios.addAll(adm);
    }

    public Optional<Usuario> login(String email, String senha) {
        return usuarios.stream()
                .filter(u -> u.getEmail().equals(email) && u.getSenha().equals(senha))
                .findFirst();
    }
}
