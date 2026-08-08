package rooms_reservation.application.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import rooms_reservation.application.enums.Sala;
import rooms_reservation.application.model_dto.Reserva;
import rooms_reservation.application.repository.impl.ReservaRepositoryImpl;

import java.time.OffsetDateTime;

import static org.junit.jupiter.api.Assertions.*;

public class ReservaRepositoryImplTest {

    private ReservaRepositoryImpl reservaRepository;

    @BeforeEach
    void setUp() {
        reservaRepository = new ReservaRepositoryImpl();
    }

    @Test
    @DisplayName("Salvar a primeira reserva do dia com sucesso")
    void salvarPrimeiraReserva() {
        OffsetDateTime inicio = OffsetDateTime.now().plusDays(1).withHour(10).withMinute(0).withSecond(0).withNano(0);
        OffsetDateTime fim = inicio.plusHours(1);

        Reserva reserva = Reserva.builder().sala(Sala.A101).inicio(inicio).fim(fim).build();

        assertDoesNotThrow(() -> reservaRepository.salvar(reserva));
        assertEquals(1, reservaRepository.buscarTodas().size());
    }

    @Test
    @DisplayName("Permitir reservas no mesmo horário em salas diferentes")
    void permitirMesmoHorarioSalasDiferentes() {
        OffsetDateTime inicio = OffsetDateTime.now().plusDays(1).withHour(14).withMinute(0).withSecond(0).withNano(0);
        OffsetDateTime fim = inicio.plusHours(1);

        Reserva r1 = Reserva.builder().sala(Sala.A101).inicio(inicio).fim(fim).build();
        Reserva r2 = Reserva.builder().sala(Sala.B201).inicio(inicio).fim(fim).build();

        assertDoesNotThrow(() -> reservaRepository.salvar(r1));
        assertDoesNotThrow(() -> reservaRepository.salvar(r2));

        assertEquals(2, reservaRepository.buscarTodas().size());
    }

    @Test
    @DisplayName("Permitir reservas contíguas na mesma sala")
    void permitirReservasContiguas() {
        OffsetDateTime inicio1 = OffsetDateTime.now().plusDays(1).withHour(10).withMinute(0).withSecond(0).withNano(0);
        OffsetDateTime fim1 = inicio1.plusHours(1);

        Reserva r1 = Reserva.builder().sala(Sala.A101).inicio(inicio1).fim(fim1).build();
        reservaRepository.salvar(r1);

        OffsetDateTime inicio2 = fim1;
        OffsetDateTime fim2 = inicio2.plusHours(1);

        Reserva r2 = Reserva.builder().sala(Sala.A101).inicio(inicio2).fim(fim2).build();

        assertDoesNotThrow(() -> reservaRepository.salvar(r2));
        assertEquals(2, reservaRepository.buscarTodas().size());
    }

    @Test
    @DisplayName("Deve barrar sobreposição de mesmos horários")
    void erroSobreposicaoTotal() {
        OffsetDateTime inicio = OffsetDateTime.now().plusDays(1).withHour(15).withMinute(0).withSecond(0).withNano(0);
        OffsetDateTime fim = inicio.plusHours(2);

        Reserva r1 = Reserva.builder().sala(Sala.C301).inicio(inicio).fim(fim).build();
        reservaRepository.salvar(r1);

        Reserva r2 = Reserva.builder().sala(Sala.C301).inicio(inicio).fim(fim).build();

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            reservaRepository.salvar(r2);
        });

        assertTrue(exception.getMessage().contains("Horário Indisponível!"));
    }

    @Test
    @DisplayName("Deve barrar sobreposição interna")
    void erroSobreposicaoParcialInterna() {
        OffsetDateTime inicioExistente = OffsetDateTime.now().plusDays(1).withHour(13).withMinute(0).withSecond(0).withNano(0);
        Reserva r1 = Reserva.builder().sala(Sala.A102).inicio(inicioExistente).fim(inicioExistente.plusHours(2)).build();
        reservaRepository.salvar(r1);

        OffsetDateTime inicioNovo = inicioExistente.plusMinutes(30);
        Reserva r2 = Reserva.builder().sala(Sala.A102).inicio(inicioNovo).fim(inicioNovo.plusHours(1)).build();

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            reservaRepository.salvar(r2);
        });

        assertTrue(exception.getMessage().contains("Horário Indisponível!"));
    }

    @Test
    @DisplayName("Deve barrar sobreposição externa")
    void erroSobreposicaoParcialExterna() {
        OffsetDateTime inicioExistente = OffsetDateTime.now().plusDays(1).withHour(14).withMinute(0).withSecond(0).withNano(0);
        Reserva r1 = Reserva.builder().sala(Sala.B203).inicio(inicioExistente).fim(inicioExistente.plusHours(1)).build();
        reservaRepository.salvar(r1);

        OffsetDateTime inicioNovo = inicioExistente.minusMinutes(30);
        Reserva r2 = Reserva.builder().sala(Sala.B203).inicio(inicioNovo).fim(inicioNovo.plusHours(2)).build();

        assertThrows(IllegalArgumentException.class, () -> {
            reservaRepository.salvar(r2);
        });
    }

    @Test
    @DisplayName("Deve barrar sobreposição sobre início")
    void erroSobreposicaoComecoMeio() {
        OffsetDateTime inicioExistente = OffsetDateTime.now().withHour(14).withMinute(0).withSecond(0).withNano(0);
        Reserva r1 = Reserva.builder().sala(Sala.A101).inicio(inicioExistente).fim(inicioExistente.plusHours(1)).build();
        reservaRepository.salvar(r1);

        OffsetDateTime inicioNovo = inicioExistente.minusMinutes(30);
        Reserva r2 = Reserva.builder().sala(Sala.A101).inicio(inicioNovo).fim(inicioNovo.plusHours(1)).build();

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            reservaRepository.salvar(r2);
        });
        assertTrue(exception.getMessage().contains("Horário Indisponível!"));
    }

    @Test
    @DisplayName("Deve barrar sobreposição sobre fim")
    void erroSobreposicaoMeioFim() {
        OffsetDateTime inicioExistente = OffsetDateTime.now().withHour(14).withMinute(0).withSecond(0).withNano(0);
        Reserva r1 = Reserva.builder().sala(Sala.A101).inicio(inicioExistente).fim(inicioExistente.plusHours(1)).build();
        reservaRepository.salvar(r1);

        OffsetDateTime inicioNovo = inicioExistente.plusMinutes(30);
        Reserva r2 = Reserva.builder().sala(Sala.A101).inicio(inicioNovo).fim(inicioNovo.plusHours(1)).build();

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            reservaRepository.salvar(r2);
        });
        assertTrue(exception.getMessage().contains("Horário Indisponível!"));
    }
}