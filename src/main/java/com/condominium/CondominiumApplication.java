package com.condominium;

import com.condominium.app.funcionario.FuncionarioApp;
import com.condominium.app.morador.MoradorApp;
import com.condominium.app.sindico.SindicoApp;
import com.condominium.model.Role;
import com.condominium.model.Usuario;
import com.condominium.service.*;

import java.util.ArrayList;
import java.util.Scanner;

public class CondominiumApplication {

    private static final Scanner scanner = new Scanner(System.in);
    private static CondominiumService condominiumService;
    private static ReservaService reservaService;
    private static ManutencaoService manutencaoService;
    private static AssembleiaService assembleiaService;
    private static AreaComumService areaService;

    public static void main(String[] args) {
        System.out.print("Digite o nome do Condomínio: ");
        String nome = scanner.nextLine();

        // inicializa serviços
        condominiumService = new CondominiumService(nome);
        reservaService      = new ReservaService();
        manutencaoService   = new ManutencaoService();
        assembleiaService   = new AssembleiaService();
        areaService         = new AreaComumService();

        // seed de um usuário de cada tipo para testes
        condominiumService.adicionarMorador(
                "João Morador", "111.111.111-11", "joao@condo.com",
                "99999-0001", "senha123", "Apt 101"
        );
        condominiumService.adicionarSindico(
                "Maria Síndica", "222.222.222-22", "maria@condo.com",
                "99999-0002", "senha123", "Bloco A"
        );
        condominiumService.adicionarAdministrador(
                "Carlos Admin", "333.333.333-33", "carlos@condo.com",
                "99999-0003", "senha123", "Financeiro"
        );

        // registra todos no serviço de autenticação
        UsuarioService us = new UsuarioService();
        us.registerAll(
                new ArrayList<>(condominiumService.getCondominium().getMoradores()),
                new ArrayList<>(condominiumService.getCondominium().getSindicos()),
                new ArrayList<>(condominiumService.getCondominium().getAdministradores())
        );

        // LOOP de login: repete até credenciais corretas
        Usuario user = null;
        while (user == null) {
            System.out.print("Email: ");
            String email = scanner.nextLine();
            System.out.print("Senha: ");
            String senha = scanner.nextLine();

            var opt = us.login(email, senha);
            if (opt.isEmpty()) {
                System.out.println("Credenciais inválidas. Tente novamente.\n");
            } else {
                user = opt.get();
            }
        }

        // despacha para o menu correto
        if (user.getRoles().contains(Role.FUNCIONARIO)) {
            FuncionarioApp.run(condominiumService,
                    reservaService,
                    manutencaoService,
                    assembleiaService);
        }
        else if (user.getRoles().contains(Role.SINDICO)) {
            SindicoApp.run(condominiumService,
                    reservaService,
                    manutencaoService,
                    assembleiaService);
        }
        else if (user.getRoles().contains(Role.MORADOR)) {
            MoradorApp.run(condominiumService,
                    areaService,
                    reservaService,
                    manutencaoService,
                    assembleiaService);
        }

        scanner.close();
    }
}
