package com.saudecardiaca.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.saudecardiaca.dto.request.LoginRequest;
import com.saudecardiaca.dto.request.RegisterRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class AuthIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private RegisterRequest createRegisterRequest() {
        RegisterRequest request = new RegisterRequest();
        request.setFirstName("Ana");
        request.setLastName("Silva");
        request.setEmail("ana@email.com");
        request.setPhone("+55 11 99999-9999");
        request.setPassword("12345678");
        request.setConfirmPassword("12345678");
        request.setBirthDate(LocalDate.of(1995, 8, 20));
        request.setGender("Feminino");
        request.setCountry("Brasil");
        return request;
    }

    @Test
    @DisplayName("POST /register - deve criar conta com sucesso")
    void register_Success() throws Exception {
        mockMvc.perform(post("/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRegisterRequest())))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.firstName").value("Ana"))
                .andExpect(jsonPath("$.email").value("ana@email.com"));
    }

    @Test
    @DisplayName("POST /register - deve retornar 422 quando senhas não conferem")
    void register_PasswordMismatch() throws Exception {
        RegisterRequest request = createRegisterRequest();
        request.setConfirmPassword("senhadiferente");

        mockMvc.perform(post("/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.codigo").value(422))
                .andExpect(jsonPath("$.mensagem").value("Senha e confirmar senha não conferem."));
    }

    @Test
    @DisplayName("POST /register - deve retornar 409 quando e-mail duplicado")
    void register_DuplicateEmail() throws Exception {
        String json = objectMapper.writeValueAsString(createRegisterRequest());

        mockMvc.perform(post("/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json));

        mockMvc.perform(post("/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.codigo").value(409));
    }

    @Test
    @DisplayName("POST /register - deve retornar 400 quando dados inválidos")
    void register_InvalidData() throws Exception {
        RegisterRequest request = new RegisterRequest();

        mockMvc.perform(post("/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.codigo").value(400))
                .andExpect(jsonPath("$.mensagem").value(
                        org.hamcrest.Matchers.startsWith("Dados do cadastro enviados incorretamente.")));
    }

    @Test
    @DisplayName("POST /login - deve fazer login com sucesso")
    void login_Success() throws Exception {
        mockMvc.perform(post("/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createRegisterRequest())));

        LoginRequest login = new LoginRequest();
        login.setEmail("ana@email.com");
        login.setPassword("12345678");

        mockMvc.perform(post("/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(login)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").exists())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.firstName").value("Ana"));
    }

    @Test
    @DisplayName("POST /login - deve retornar 404 quando usuário não encontrado")
    void login_UserNotFound() throws Exception {
        LoginRequest login = new LoginRequest();
        login.setEmail("naoexiste@email.com");
        login.setPassword("12345678");

        mockMvc.perform(post("/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(login)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.codigo").value(404));
    }

    @Test
    @DisplayName("POST /login - deve retornar 401 quando senha incorreta")
    void login_WrongPassword() throws Exception {
        mockMvc.perform(post("/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createRegisterRequest())));

        LoginRequest login = new LoginRequest();
        login.setEmail("ana@email.com");
        login.setPassword("senhaerrada");

        mockMvc.perform(post("/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(login)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.codigo").value(401));
    }
}
