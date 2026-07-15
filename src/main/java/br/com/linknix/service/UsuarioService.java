package br.com.linknix.service;

import br.com.linknix.dto.UsuarioResponseDTO;
import br.com.linknix.entity.Usuario;
import br.com.linknix.exception.RecursoNaoEncontradoException;
import br.com.linknix.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;

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

    private UsuarioResponseDTO converterParaResponse(Usuario usuario) {
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
