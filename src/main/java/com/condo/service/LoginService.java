// src/main/java/com/condo/service/LoginService.java
package com.condo.service;

import com.condo.domain.Usuario;
import com.condo.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class    LoginService {

    private final UsuarioRepository usuarioRepository;

    @Autowired
    public LoginService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    public Optional<Usuario> autenticar(String email, String senha) {
        // Lógica de autenticação (ex: verificar hash da senha, etc.)
        return usuarioRepository.findByEmailAndSenha(email, senha);
    }
}
