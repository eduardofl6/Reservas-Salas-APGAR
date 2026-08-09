package rooms_reservation.application.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import rooms_reservation.application.enums.Sala;
import rooms_reservation.application.model_dto.Estatisticas;
import rooms_reservation.application.model_dto.Reserva;
import rooms_reservation.application.repository.ReservaRepository;
import rooms_reservation.application.service.impl.ReservaServiceImpl;

import java.util.Collections;
import java.util.List;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.time.OffsetDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ReservaServiceImplTest {

    @Mock
    private ReservaRepository reservaRepository;

    @InjectMocks
    private ReservaServiceImpl reservaService;

    @Test
    @DisplayName("Salvar uma reserva com sucesso quando todas as regras forem atendidas")
    void SalvaReservaComSucessoComCamposCorretos() {
        OffsetDateTime inicio = OffsetDateTime.now().plusDays(1).withHour(14).withMinute(0).withSecond(0).withNano(0);
        OffsetDateTime fim = inicio.plusHours(1);

        Reserva reservaValida = Reserva.builder()
                .sala(Sala.A101)
                .responsavel("Eduardo")
                .inicio(inicio)
                .fim(fim)
                .build();

        assertDoesNotThrow(() -> reservaService.salvar(reservaValida));

        verify(reservaRepository, times(1)).salvar(reservaValida);
    }

    @Test
    @DisplayName("Apresentar exceção quando duracação menor que 30 minutos")
    void erroDuracaoMinima() {
        OffsetDateTime inicio = OffsetDateTime.now().plusDays(1).withHour(8).withMinute(0).withSecond(0).withNano(0);
        OffsetDateTime fim = inicio.plusMinutes(29);

        Reserva reservaInvalida = Reserva.builder()
                .sala(Sala.B201)
                .responsavel("Eduardo")
                .inicio(inicio)
                .fim(fim)
                .build();

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            reservaService.salvar(reservaInvalida);
        });

        assertEquals("A reserva deve durar entre 30 minutos à 2 horas", exception.getMessage());

        verify(reservaRepository, times(0)).salvar(reservaInvalida);

    }

    @Test
    @DisplayName("Apresentar exceção quando duração maior que duas horas")
    void erroDuracaoMaxima() {
        OffsetDateTime inicio = OffsetDateTime.now().plusDays(1).withHour(8).withMinute(0).withSecond(0).withNano(0);
        OffsetDateTime fim = inicio.plusMinutes(120).plusNanos(1);

        Reserva reservaInvalida = Reserva.builder()
                .sala(Sala.B201)
                .responsavel("Eduardo")
                .inicio(inicio)
                .fim(fim)
                .build();

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            reservaService.salvar(reservaInvalida);
        });
        assertEquals("A reserva deve durar entre 30 minutos à 2 horas", exception.getMessage());
        verify(reservaRepository, times(0)).salvar(reservaInvalida);
    }

    @Test
    @DisplayName("Apresentar exceção quando inicio maior que fim")
    void erroFimAntesDeInicio() {
        OffsetDateTime inicio = OffsetDateTime.now().withHour(15).plusDays(1).withMinute(0).withSecond(0).withNano(0);
        OffsetDateTime fim = inicio.minusNanos(1);

        Reserva reservaInvalida = Reserva.builder()
                .sala(Sala.B201)
                .responsavel("Eduardo")
                .inicio(inicio)
                .fim(fim)
                .build();

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            reservaService.salvar(reservaInvalida);
        });
        assertEquals("O horário de fim deve ser posterior ao horário de início.", exception.getMessage());
        verify(reservaRepository, times(0)).salvar(reservaInvalida);
    }

    @Test
    @DisplayName("Apresentar exceção quando fim superior ao horário limite")
    void erroHorarioSuperiorAoLimite() {
        OffsetDateTime fim = OffsetDateTime.now().plusDays(1).withHour(18).withMinute(0).withSecond(0).withNano(1);;
        OffsetDateTime inicio = fim.withHour(17).withMinute(30);

        Reserva reservaInvalida = Reserva.builder()
                .sala(Sala.B201)
                .responsavel("Eduardo")
                .inicio(inicio)
                .fim(fim)
                .build();


        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            reservaService.salvar(reservaInvalida);
        });
        assertEquals("O horário de fim deve estar entre 08:30 e 18:00.", exception.getMessage());
        verify(reservaRepository, times(0)).salvar(reservaInvalida);
    }

    @Test
    @DisplayName("Apresentar exceção quando inicio inferior ao horário limite")
    void erroHorarioInferiorAoLimite() {
        OffsetDateTime inicio = OffsetDateTime.now().plusDays(1).withHour(7).withMinute(59).withSecond(59).withNano(9999999);
        OffsetDateTime fim = inicio.withHour(9);;

        Reserva reservaInvalida = Reserva.builder()
                .sala(Sala.B201)
                .responsavel("Eduardo")
                .inicio(inicio)
                .fim(fim)
                .build();


        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            reservaService.salvar(reservaInvalida);
        });
        assertEquals("O horário de início deve estar entre 08:00 e 17:30.", exception.getMessage());
        verify(reservaRepository, times(0)).salvar(reservaInvalida);
    }

    @Test
    @DisplayName("Apresentar exceção quando a reserva for para o passado (retroativa)")
    void erroReservaRetroativa() {
        OffsetDateTime inicio = OffsetDateTime.now().minusDays(1).withHour(14).withMinute(0).withSecond(0).withNano(0);
        OffsetDateTime fim = inicio.plusHours(1);

        Reserva reservaInvalida = Reserva.builder()
                .sala(Sala.B201)
                .responsavel("Eduardo")
                .inicio(inicio)
                .fim(fim)
                .build();

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            reservaService.salvar(reservaInvalida);
        });

        assertEquals("As reservas retroativas são proibidas", exception.getMessage());
        verify(reservaRepository, times(0)).salvar(reservaInvalida);
    }

    @Test
    @DisplayName("Apresentar exceção quando o início não for em intervalo exato de 30 minutos")
    void erroIntervaloInvalido() {
        OffsetDateTime inicio = OffsetDateTime.now().plusDays(1).withHour(10).withMinute(15).withSecond(0).withNano(0);
        OffsetDateTime fim = inicio.plusMinutes(45);

        Reserva reservaInvalida = Reserva.builder()
                .sala(Sala.B201)
                .responsavel("Eduardo")
                .inicio(inicio)
                .fim(fim)
                .build();

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            reservaService.salvar(reservaInvalida);
        });

        assertEquals("Reservas devem ter início em intervalos de 30 minutos (ex: 10:00, 10:30)", exception.getMessage());
        verify(reservaRepository, times(0)).salvar(reservaInvalida);
    }

    @Test
    @DisplayName("Repassar a chamada do listar para o repositório")
    void listarReservasComSucesso() {
        reservaService.listarReservas();
        verify(reservaRepository, times(1)).buscarTodas();
    }

    @Test
    @DisplayName("Repassar a chamada do limpar para o repositório")
    void limparComSucesso() {
        reservaService.limpar();
        verify(reservaRepository, times(1)).limpar();
    }

    @Test
    @DisplayName("Retornar estatísticas zeradas quando não houver reservas no dia")
    void listarEstatisticasVazias() {
        when(reservaRepository.buscarData(any())).thenReturn(Collections.emptyList());

        Estatisticas estatisticas = reservaService.listarEstatisticasDia();

        assertEquals(0, estatisticas.getCountReservas());
        assertEquals(0, estatisticas.getSalasUtilizadas());
        assertEquals(0L, estatisticas.getTempoTotalReservadoMinutos());
        assertEquals(0.0, estatisticas.getMediaDuracaoMinutos());
        assertEquals(0L, estatisticas.getMaiorDuracaoMinutos());
    }

    @Test
    @DisplayName("Calcular estatísticas matemáticas corretamente baseando-se nas reservas do dia")
    void listarEstatisticasComDados() {
        OffsetDateTime inicio1 = OffsetDateTime.now().plusDays(1).withHour(10).withMinute(0).withSecond(0).withNano(0);
        Reserva r1 = Reserva.builder().sala(Sala.A101).responsavel("João").inicio(inicio1).fim(inicio1.plusMinutes(60)).build();

        OffsetDateTime inicio2 = OffsetDateTime.now().plusDays(1).withHour(14).withMinute(0).withSecond(0).withNano(0);
        Reserva r2 = Reserva.builder().sala(Sala.B201).responsavel("Maria").inicio(inicio2).fim(inicio2.plusMinutes(90)).build();

        OffsetDateTime inicio3 = OffsetDateTime.now().plusDays(1).withHour(16).withMinute(0).withSecond(0).withNano(0);
        Reserva r3 = Reserva.builder().sala(Sala.A101).responsavel("Jose").inicio(inicio3).fim(inicio3.plusMinutes(30)).build();

        List<Reserva> listaReservasDia = List.of(r1, r2, r3);

        when(reservaRepository.buscarData(any())).thenReturn(listaReservasDia);

        Estatisticas estatisticas = reservaService.listarEstatisticasDia();

        assertEquals(3, estatisticas.getCountReservas());
        assertEquals(2, estatisticas.getSalasUtilizadas());
        assertEquals(180L, estatisticas.getTempoTotalReservadoMinutos());
        assertEquals(60.0, estatisticas.getMediaDuracaoMinutos());
        assertEquals(90L, estatisticas.getMaiorDuracaoMinutos());
    }




}
