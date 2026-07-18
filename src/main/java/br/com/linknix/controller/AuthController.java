package br.com.linknix.controller;

import br.com.linknix.dto.BootstrapUsuarioRequestDTO;
import br.com.linknix.dto.LoginRequestDTO;
import br.com.linknix.dto.LoginResponseDTO;
import br.com.linknix.dto.UsuarioResponseDTO;
import br.com.linknix.service.AuthService;
import br.com.linknix.service.UsuarioService;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@SecurityRequirements
public class AuthController {

    private final AuthService authService;
    private final UsuarioService usuarioService;

    @PostMapping("/bootstrap")
    public ResponseEntity<UsuarioResponseDTO> bootstrap(
            @Valid @RequestBody BootstrapUsuarioRequestDTO request
    ) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(usuarioService.criarPrimeiroAdministrador(request));
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(
            @Valid @RequestBody LoginRequestDTO request
    ) {
        return ResponseEntity.ok(authService.login(request));
    }
}
