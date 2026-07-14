package br.com.linknix.dto;

import br.com.linknix.enums.PerfilUsuario;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class UsuarioRequestDTO {
    @NotBlank @Size(max = 150)
    private String nome;
    @NotBlank @Email @Size(max = 180)
    private String email;
    @NotBlank @Size(min = 8, max = 100)
    private String senha;
    @NotNull
    private PerfilUsuario perfil;
}
