package br.com.linknix.service;

import br.com.linknix.dto.LoginRequestDTO;
import br.com.linknix.dto.LoginResponseDTO;
import br.com.linknix.entity.Usuario;
import br.com.linknix.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final UsuarioService usuarioService;
    private final JwtService jwtService;

    public LoginResponseDTO login(LoginRequestDTO request) {
        String email = request.getEmail().trim().toLowerCase();
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(email, request.getSenha())
        );
        Usuario usuario = usuarioService.buscarAtivoPorEmail(email);

        return LoginResponseDTO.builder()
                .token(jwtService.gerarToken(usuario))
                .tipo("Bearer")
                .expiraEmSegundos(jwtService.getExpiracaoSegundos())
                .usuario(usuarioService.converterParaResponse(usuario))
                .build();
    }
}
