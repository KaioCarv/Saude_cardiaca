package com.saudecardiaca.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.saudecardiaca.dto.request.HeartHealthRecordRequest;
import com.saudecardiaca.dto.request.LoginRequest;
import com.saudecardiaca.dto.request.RegisterRequest;
import com.saudecardiaca.dto.response.LoginResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class HeartHealthIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private String authToken;

    @BeforeEach
    void setUp() throws Exception {
        RegisterRequest register = new RegisterRequest();
        register.setFirstName("Ana");
        register.setLastName("Silva");
        register.setEmail("ana@email.com");
        register.setPhone("+55 11 99999-9999");
        register.setPassword("12345678");
        register.setConfirmPassword("12345678");
        register.setBirthDate(LocalDate.of(1995, 8, 20));
        register.setGender("Feminino");
        register.setCountry("Brasil");

        mockMvc.perform(post("/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(register)));

        LoginRequest login = new LoginRequest();
        login.setEmail("ana@email.com");
        login.setPassword("12345678");

        MvcResult result = mockMvc.perform(post("/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(login)))
                .andReturn();

        LoginResponse loginResponse = objectMapper.readValue(
                result.getResponse().getContentAsString(), LoginResponse.class);
        authToken = loginResponse.getToken();
    }

    private HeartHealthRecordRequest createRecordRequest() {
        HeartHealthRecordRequest request = new HeartHealthRecordRequest();
        request.setBloodPressureSystolic(120);
        request.setBloodPressureDiastolic(80);
        request.setHeartRate(72);
        request.setOxygenSaturation(98.0);
        request.setBodyWeight(68.5);
        request.setSymptoms(List.of("falta de ar", "tontura"));
        request.setRecordedAt(LocalDateTime.of(2026, 4, 14, 9, 30));
        return request;
    }

    @Test
    @DisplayName("POST /heart-health-records - deve criar registro com autenticação")
    void createRecord_Success() throws Exception {
        mockMvc.perform(post("/heart-health-records")
                        .header("Authorization", "Bearer " + authToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRecordRequest())))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.bloodPressureSystolic").value(120))
                .andExpect(jsonPath("$.heartRate").value(72));
    }

    @Test
    @DisplayName("POST /heart-health-records - deve retornar 401 sem token")
    void createRecord_Unauthorized() throws Exception {
        mockMvc.perform(post("/heart-health-records")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRecordRequest())))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.codigo").value(401))
                .andExpect(jsonPath("$.mensagem").value("Usuário não autenticado."));
    }

    @Test
    @DisplayName("GET /heart-health-records - deve retornar histórico")
    void getRecords_Success() throws Exception {
        mockMvc.perform(post("/heart-health-records")
                .header("Authorization", "Bearer " + authToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createRecordRequest())));

        mockMvc.perform(get("/heart-health-records")
                        .header("Authorization", "Bearer " + authToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.items").isArray())
                .andExpect(jsonPath("$.items[0].bloodPressureSystolic").value(120));
    }

    @Test
    @DisplayName("GET /heart-health-records - deve retornar 404 sem registros")
    void getRecords_NotFound() throws Exception {
        mockMvc.perform(get("/heart-health-records")
                        .header("Authorization", "Bearer " + authToken))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.codigo").value(404));
    }

    @Test
    @DisplayName("GET /heart-health-reports - deve gerar relatório")
    void getReport_Success() throws Exception {
        mockMvc.perform(post("/heart-health-records")
                .header("Authorization", "Bearer " + authToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createRecordRequest())));

        mockMvc.perform(get("/heart-health-reports")
                        .header("Authorization", "Bearer " + authToken)
                        .param("startDate", "01/04/2026")
                        .param("endDate", "30/04/2026"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.averageBloodPressure.systolic").value(120))
                .andExpect(jsonPath("$.averageHeartRate").exists())
                .andExpect(jsonPath("$.riskLevel").exists())
                .andExpect(jsonPath("$.symptomOccurrences").exists());
    }

    @Test
    @DisplayName("GET /heart-health-reports - deve retornar 404 sem dados")
    void getReport_NoData() throws Exception {
        mockMvc.perform(get("/heart-health-reports")
                        .header("Authorization", "Bearer " + authToken)
                        .param("startDate", "01/01/2025")
                        .param("endDate", "31/01/2025"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.codigo").value(404));
    }
}
