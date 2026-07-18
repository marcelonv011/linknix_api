package br.com.linknix.service;

import br.com.linknix.dto.BootstrapUsuarioRequestDTO;
import br.com.linknix.dto.UsuarioRequestDTO;
import br.com.linknix.dto.UsuarioResponseDTO;
import br.com.linknix.entity.Usuario;
import br.com.linknix.enums.PerfilUsuario;
import br.com.linknix.exception.ConflitoException;
import br.com.linknix.exception.RecursoNaoEncontradoException;
import br.com.linknix.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public UsuarioResponseDTO criarPrimeiroAdministrador(
            BootstrapUsuarioRequestDTO request
    ) {
        if (usuarioRepository.count() > 0) {
            throw new ConflitoException(
                    "O administrador inicial já foi criado"
            );
        }

        Usuario usuario = construirUsuario(
                request.getNome(),
                request.getEmail(),
                request.getSenha(),
                PerfilUsuario.ADMINISTRADOR
        );
        return converterParaResponse(usuarioRepository.save(usuario));
    }

    @Transactional
    public UsuarioResponseDTO criar(UsuarioRequestDTO request) {
        Usuario usuario = construirUsuario(
                request.getNome(),
                request.getEmail(),
                request.getSenha(),
                request.getPerfil()
        );
        return converterParaResponse(usuarioRepository.save(usuario));
    }

    @Transactional(readOnly = true)
    public List<UsuarioResponseDTO> listarTodos() {
        return usuarioRepository.findAll().stream()
                .map(this::converterParaResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public UsuarioResponseDTO buscarPorId(Long id) {
        return converterParaResponse(buscarEntidadePorId(id));
    }

    Usuario buscarEntidadePorId(Long id) {
        return usuarioRepository.findById(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException(
                        "Usuário não encontrado com o ID " + id
                ));
    }

    Usuario buscarAtivoPorEmail(String email) {
        return usuarioRepository.findByEmailIgnoreCaseAndAtivoTrue(email)
                .orElseThrow(() -> new RecursoNaoEncontradoException(
                        "Usuário ativo não encontrado"
                ));
    }

    private Usuario construirUsuario(
            String nome,
            String email,
            String senha,
            PerfilUsuario perfil
    ) {
        validarEmailDisponivel(email);
        return Usuario.builder()
                .nome(nome.trim())
                .email(email.trim().toLowerCase())
                .senhaHash(passwordEncoder.encode(senha))
                .perfil(perfil)
                .ativo(true)
                .build();
    }

    private void validarEmailDisponivel(String email) {
        if (usuarioRepository.existsByEmailIgnoreCase(email.trim())) {
            throw new ConflitoException("Já existe um usuário com este e-mail");
        }
    }

    UsuarioResponseDTO converterParaResponse(Usuario usuario) {
        return UsuarioResponseDTO.builder()
                .id(usuario.getId())
                .nome(usuario.getNome())
                .email(usuario.getEmail())
                .perfil(usuario.getPerfil())
                .ativo(usuario.getAtivo())
                .criadoEm(usuario.getCriadoEm())
                .build();
    }
}
