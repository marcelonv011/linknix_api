package br.com.linknix.security;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class SecurityApiTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void deveCriarPrimeiroAdminFazerLoginEUsarJwt() throws Exception {
        String bootstrapJson = """
                {
                  "nome": "Administrador",
                  "email": "admin@linknix.com",
                  "senha": "SenhaSegura123"
                }
                """;

        mockMvc.perform(post("/api/auth/bootstrap")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(bootstrapJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.perfil").value("ADMINISTRADOR"));

        mockMvc.perform(get("/api/admin/usuarios"))
                .andExpect(status().isUnauthorized());

        String loginResponse = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "email": "admin@linknix.com",
                                  "senha": "SenhaSegura123"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.tipo").value("Bearer"))
                .andExpect(jsonPath("$.token").isNotEmpty())
                .andReturn()
                .getResponse()
                .getContentAsString();

        String token = loginResponse
                .replaceFirst(".*\\\"token\\\":\\\"([^\\\"]+)\\\".*", "$1");

        mockMvc.perform(get("/api/admin/usuarios")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].email").value("admin@linknix.com"));
    }

    @Test
    void pesquisadorDeveExecutarTestesSemAcessarAdministracao() throws Exception {
        mockMvc.perform(post("/api/auth/bootstrap")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "nome": "Administrador",
                                  "email": "admin-pesquisa@linknix.com",
                                  "senha": "SenhaSegura123"
                                }
                                """))
                .andExpect(status().isCreated());

        String loginAdmin = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "email": "admin-pesquisa@linknix.com",
                                  "senha": "SenhaSegura123"
                                }
                                """))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        String tokenAdmin = extrairToken(loginAdmin);

        mockMvc.perform(post("/api/admin/usuarios")
                        .header("Authorization", "Bearer " + tokenAdmin)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "nome": "Pesquisadora",
                                  "email": "pesquisadora@linknix.com",
                                  "senha": "SenhaSegura123",
                                  "perfil": "PESQUISADOR"
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.perfil").value("PESQUISADOR"));

        String loginPesquisador = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "email": "pesquisadora@linknix.com",
                                  "senha": "SenhaSegura123"
                                }
                                """))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        String tokenPesquisador = extrairToken(loginPesquisador);

        mockMvc.perform(post("/api/pesquisa/execucoes-teste")
                        .header("Authorization", "Bearer " + tokenPesquisador)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "nome": "Teste controlado",
                                  "descricao": "Execução criada por pesquisador"
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.nome").value("Teste controlado"));

        mockMvc.perform(get("/api/pesquisa/execucoes-teste")
                        .header("Authorization", "Bearer " + tokenPesquisador))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].nome").value("Teste controlado"));

        mockMvc.perform(get("/api/resultados")
                        .header("Authorization", "Bearer " + tokenPesquisador))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/pesquisa/prompts")
                        .header("Authorization", "Bearer " + tokenPesquisador))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/pesquisa/modelos")
                        .header("Authorization", "Bearer " + tokenPesquisador))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/admin/usuarios")
                        .header("Authorization", "Bearer " + tokenPesquisador))
                .andExpect(status().isForbidden());

        mockMvc.perform(get("/api/admin/prompts")
                        .header("Authorization", "Bearer " + tokenPesquisador))
                .andExpect(status().isForbidden());

        mockMvc.perform(get("/api/admin/modelos")
                        .header("Authorization", "Bearer " + tokenPesquisador))
                .andExpect(status().isForbidden());
    }

    @Test
    void pesquisadorDeveSeRegistrarComSuaPropriaSenha() throws Exception {
        mockMvc.perform(post("/api/auth/registro-pesquisador")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "nome": "Pesquisador visitante",
                                  "email": "visitante@linknix.com",
                                  "senha": "MinhaSenhaSegura123"
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.perfil").value("PESQUISADOR"))
                .andExpect(jsonPath("$.email").value("visitante@linknix.com"));

        String loginResponse = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "email": "visitante@linknix.com",
                                  "senha": "MinhaSenhaSegura123"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.usuario.perfil").value("PESQUISADOR"))
                .andReturn().getResponse().getContentAsString();

        mockMvc.perform(get("/api/admin/usuarios")
                        .header(
                                "Authorization",
                                "Bearer " + extrairToken(loginResponse)
                        ))
                .andExpect(status().isForbidden());
    }

    private String extrairToken(String loginResponse) {
        return loginResponse.replaceFirst(
                ".*\\\"token\\\":\\\"([^\\\"]+)\\\".*",
                "$1"
        );
    }
}
