package rooms_reservation.application.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import rooms_reservation.application.enums.Sala;
import rooms_reservation.application.model_dto.Reserva;
import rooms_reservation.application.repository.ReservaRepository;
import tools.jackson.databind.ObjectMapper;
import static org.hamcrest.Matchers.containsString;

import java.time.OffsetDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class ReservaControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ReservaRepository reservaRepository;

    @BeforeEach
    void setUp() {
        reservaRepository.limpar();
    }

    @Test
    @DisplayName("Retornar 201 Created ao salvar uma reserva válida via API")
    void criarReservaComSucesso() throws Exception {
        OffsetDateTime inicio = OffsetDateTime.now().plusDays(1).withHour(10).withMinute(0).withSecond(0).withNano(0);

        Reserva reserva = Reserva.builder()
                .sala(Sala.A101)
                .responsavel("Eduardo")
                .inicio(inicio)
                .fim(inicio.plusHours(1))
                .build();

        String payload = objectMapper.writeValueAsString(reserva);

        mockMvc.perform(post("/reservas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isCreated());
    }

    @Test
    @DisplayName("Retornar 422 Unprocessable Entity regra de duração for violada")
    void erroDuracaoMenorQueMinima() throws Exception {
        OffsetDateTime inicio = OffsetDateTime.now().plusDays(1).withHour(10).withMinute(0).withSecond(0).withNano(0);

        Reserva reservaInvalida = Reserva.builder()
                .sala(Sala.B201)
                .responsavel("Eduardo")
                .inicio(inicio)
                .fim(inicio.plusMinutes(15))
                .build();

        String payload = objectMapper.writeValueAsString(reservaInvalida);

        mockMvc.perform(post("/reservas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.erro").value("A reserva deve durar entre 30 minutos à 2 horas"));
    }

    @Test
    @DisplayName("Retornar 422 Unprocessable Entity ao tentar sobrepor reservas na mesma sala")
    void erroConflitoDeHorario() throws Exception {
        OffsetDateTime inicio = OffsetDateTime.now().plusDays(1).withHour(14).withMinute(0).withSecond(0).withNano(0);

        Reserva reserva1 = Reserva.builder()
                .sala(Sala.C301)
                .responsavel("Eduardo")
                .inicio(inicio)
                .fim(inicio.plusHours(1))
                .build();

        reservaRepository.salvar(reserva1);

        Reserva reserva2 = Reserva.builder()
                .sala(Sala.C301)
                .responsavel("Invasor")
                .inicio(inicio)
                .fim(inicio.plusHours(1))
                .build();

        String payload = objectMapper.writeValueAsString(reserva2);

        mockMvc.perform(post("/reservas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.erro").value(containsString("Horário Indisponível!")));
    }

    @Test
    @DisplayName("Retornar 200 OK com as estatísticas atualizadas")
    void listarEstatisticasComSucesso() throws Exception {
        OffsetDateTime inicio = OffsetDateTime.now().withHour(15).withMinute(0).withSecond(0).withNano(0);

        Reserva reserva = Reserva.builder()
                .sala(Sala.A101)
                .responsavel("Eduardo")
                .inicio(inicio)
                .fim(inicio.plusHours(1))
                .build();

        reservaRepository.salvar(reserva);

        mockMvc.perform(get("/estatisticas")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.countReservas").value(1))
                .andExpect(jsonPath("$.tempoTotalReservadoMinutos").value(60));
    }

    @Test
    @DisplayName("Retorna 200 OK quando não existem reservas")
    void listarReservasVazias() throws Exception {
        reservaRepository.limpar();

        mockMvc.perform(get("/reservas")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").isEmpty());
    }

    @Test
    @DisplayName("Deve retornar 200 OK e a lista de reservas cadastradas")
    void listarReservasComDados() throws Exception {
        OffsetDateTime inicio = OffsetDateTime.now().withHour(10).withMinute(0).withSecond(0).withNano(0);
        Reserva reserva = Reserva.builder().sala(Sala.A101).responsavel("Eduardo").inicio(inicio).fim(inicio.plusHours(1)).build();
        reservaRepository.salvar(reserva);

        mockMvc.perform(get("/reservas")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].responsavel").value("Eduardo"));
    }

    @Test
    @DisplayName("Deve retornar 200 OK ao deletar todas as reservas")
    void limparReservasComSucesso() throws Exception {
        OffsetDateTime inicio = OffsetDateTime.now().withHour(10).withMinute(0).withSecond(0).withNano(0);
        Reserva reserva = Reserva.builder().sala(Sala.A101).responsavel("Eduardo").inicio(inicio).fim(inicio.plusHours(1)).build();
        reservaRepository.salvar(reserva);

        mockMvc.perform(delete("/reservas"))
                .andExpect(status().isOk());

        assertEquals(0, reservaRepository.buscarTodas().size());
    }
}