package com.condo.service;

import com.condo.domain.Morador;
import com.condo.domain.Role; // Adicionei, pois é usado em cadastrarNovoMorador
import com.condo.domain.Usuario;
import com.condo.repository.MoradorRepository;
import com.condo.repository.UsuarioRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet; // Adicionei, pois é usado em cadastrarNovoMorador
import java.util.List;
import java.util.Optional;
import java.util.Set; // Adicionei, pois é usado em cadastrarNovoMorador

@Service
public class UsuarioService {

    private static final Logger log = LoggerFactory.getLogger(UsuarioService.class);

    private final UsuarioRepository usuarioRepository; // Para operações gerais de Usuário (ex: email único)
    private final MoradorRepository moradorRepository; // Para operações específicas de Morador

    @Autowired
    public UsuarioService(UsuarioRepository usuarioRepository, MoradorRepository moradorRepository) {
        this.usuarioRepository = usuarioRepository;
        this.moradorRepository = moradorRepository;
    }

    @Transactional
    public Morador cadastrarNovoMorador(String nome, String email, String senha, String telefone, String cpf, String unidade) {
        if (nome == null || nome.trim().isEmpty()) throw new IllegalArgumentException("O nome do morador não pode ser vazio.");
        if (email == null || email.trim().isEmpty()) throw new IllegalArgumentException("O email do morador não pode ser vazio.");
        if (senha == null || senha.trim().isEmpty()) throw new IllegalArgumentException("A senha do morador não pode ser vazia.");
        if (cpf == null || cpf.trim().isEmpty()) throw new IllegalArgumentException("O CPF do morador não pode ser vazio.");
        if (unidade == null || unidade.trim().isEmpty()) throw new IllegalArgumentException("A unidade do morador não pode ser vazia.");

        Optional<Usuario> usuarioExistenteComEmail = usuarioRepository.findByEmail(email.trim());
        if (usuarioExistenteComEmail.isPresent()) {
            throw new IllegalArgumentException("O email '" + email.trim() + "' já está cadastrado no sistema.");
        }

        Optional<Morador> moradorExistenteComCpf = moradorRepository.findByCpf(cpf.trim());
        if (moradorExistenteComCpf.isPresent()) {
            throw new IllegalArgumentException("O CPF '" + cpf.trim() + "' já está cadastrado para outro morador.");
        }

        Morador novoMorador = new Morador();
        novoMorador.setNome(nome.trim());
        novoMorador.setEmail(email.trim());
        novoMorador.setSenha(senha); // Lembre-se da criptografia em um sistema real
        novoMorador.setTelefone(telefone != null ? telefone.trim() : null);
        novoMorador.setCpf(cpf.trim());
        novoMorador.setUnidade(unidade.trim());

        Set<Role> roles = new HashSet<>();
        roles.add(Role.MORADOR); // Define a role padrão para novo morador
        novoMorador.setRoles(roles);

        // Se houver um campo 'condominio' na entidade Usuario/Morador, ele precisaria ser setado aqui.
        // Ex: novoMorador.setCondominio(condominiumRepository.findById(ID_CONDOMINIO_PADRAO).orElse(null));

        log.info("Cadastrando novo morador: Nome='{}', Email='{}', Unidade='{}', CPF='{}'",
                novoMorador.getNome(), novoMorador.getEmail(), novoMorador.getUnidade(), novoMorador.getCpf());
        return moradorRepository.save(novoMorador);
    }

    public List<Morador> listarTodosMoradores() {
        log.debug("Listando todos os moradores.");
        return moradorRepository.findAll();
    }

    /**
     * Busca um morador pelo seu ID.
     * @param id O ID do morador.
     * @return Um Optional contendo o Morador se encontrado.
     */
    public Optional<Morador> buscarMoradorPorId(Long id) {
        if (id == null) {
            log.warn("Tentativa de busca de morador com ID nulo.");
            return Optional.empty();
        }
        return moradorRepository.findById(id);
    }

    public Optional<Morador> buscarMoradorPorIdentificador(String identificador) {
        if (identificador == null || identificador.trim().isEmpty()) {
            log.warn("Tentativa de busca de morador com identificador vazio.");
            return Optional.empty();
        }
        String trimmedIdentificador = identificador.trim();

        Optional<Morador> porCpf = moradorRepository.findByCpf(trimmedIdentificador);
        if (porCpf.isPresent()) {
            log.info("Morador encontrado por CPF: {}", trimmedIdentificador);
            return porCpf;
        }

        // Assumindo que moradorRepository.findByUnidade retorna List<Morador>
        // Se for Optional<Morador>, a lógica abaixo muda.
        List<Morador> porUnidade = moradorRepository.findByUnidadeIgnoreCase(trimmedIdentificador); // Supondo que exista e seja case-insensitive
        if (!porUnidade.isEmpty()) {
            if (porUnidade.size() > 1) {
                log.warn("Múltiplos moradores encontrados para a unidade '{}'. Retornando o primeiro.", trimmedIdentificador);
            }
            log.info("Morador encontrado por unidade: {}", trimmedIdentificador);
            return Optional.of(porUnidade.get(0));
        }

        // Opcional: Buscar por email se for um identificador válido
        Optional<Usuario> porEmailUsuario = usuarioRepository.findByEmail(trimmedIdentificador);
        if (porEmailUsuario.isPresent() && porEmailUsuario.get() instanceof Morador) {
            log.info("Morador encontrado por email: {}", trimmedIdentificador);
            return Optional.of((Morador) porEmailUsuario.get());
        }


        log.info("Nenhum morador encontrado com o identificador: {}", trimmedIdentificador);
        return Optional.empty();
    }

    @Transactional
    public Morador atualizarMorador(Long moradorId, String novoNome, String novoEmail, String novoTelefone, String novaUnidade) {
        log.info("Atualizando morador ID: {}", moradorId);
        // Usa o método buscarMoradorPorId que acabamos de definir/confirmar
        Morador moradorParaAtualizar = buscarMoradorPorId(moradorId)
                .orElseThrow(() -> new IllegalArgumentException("Morador com ID " + moradorId + " não encontrado."));

        boolean modificado = false;

        if (novoNome != null && !novoNome.trim().isEmpty() && !novoNome.trim().equals(moradorParaAtualizar.getNome())) {
            moradorParaAtualizar.setNome(novoNome.trim());
            modificado = true;
        }
        if (novoEmail != null && !novoEmail.trim().isEmpty() && !novoEmail.trim().equalsIgnoreCase(moradorParaAtualizar.getEmail())) {
            // Validação de email único (se o email foi alterado)
            // Verifica se o novo email já existe para OUTRO usuário
            Optional<Usuario> usuarioComNovoEmail = usuarioRepository.findByEmail(novoEmail.trim());
            if (usuarioComNovoEmail.isPresent() && !usuarioComNovoEmail.get().getId().equals(moradorId)) {
                throw new IllegalArgumentException("O novo email '" + novoEmail.trim() + "' já está cadastrado para outro usuário.");
            }
            moradorParaAtualizar.setEmail(novoEmail.trim());
            modificado = true;
        }
        if (novoTelefone != null && !novoTelefone.trim().isEmpty() && !novoTelefone.trim().equals(moradorParaAtualizar.getTelefone())) {
            moradorParaAtualizar.setTelefone(novoTelefone.trim());
            modificado = true;
        }
        if (novaUnidade != null && !novaUnidade.trim().isEmpty() && !novaUnidade.trim().equals(moradorParaAtualizar.getUnidade())) {
            moradorParaAtualizar.setUnidade(novaUnidade.trim());
            modificado = true;
        }

        if (modificado) {
            log.info("Salvando alterações para o morador ID: {}", moradorId);
            // moradorParaAtualizar.setAtualizadoEm(LocalDateTime.now()); // Se tiver campo de auditoria
            return moradorRepository.save(moradorParaAtualizar); // Usar MoradorRepository para salvar Morador
        } else {
            log.info("Nenhuma alteração detectada para o morador ID: {}", moradorId);
            return moradorParaAtualizar;
        }
    }

    @Transactional
    public void excluirMorador(Long moradorId) {
        log.info("Tentando excluir morador ID: {}", moradorId);
        // Primeiro, verifica se existe um morador com esse ID usando MoradorRepository
        Morador moradorParaExcluir = moradorRepository.findById(moradorId)
                .orElseThrow(() -> new IllegalArgumentException("Morador com ID " + moradorId + " não encontrado para exclusão."));

        // TODO: Lógica de pré-exclusão (importante em sistemas reais):
        // - Verificar se o morador tem pendências (pagamentos ativos, reservas futuras).
        // - Se houver, impedir a exclusão ou realizar ações compensatórias.
        // - Remover de forma cascata ou anular referências em outras tabelas.

        moradorRepository.delete(moradorParaExcluir); // Usar MoradorRepository para deletar Morador
        log.info("Morador ID: {} excluído com sucesso.", moradorId);
    }

    // REMOVA as versões comentadas de atualizarMorador e excluirMorador do final do seu arquivo original.
}