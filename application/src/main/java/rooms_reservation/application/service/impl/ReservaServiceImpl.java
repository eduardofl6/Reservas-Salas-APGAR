package rooms_reservation.application.service.impl;

import org.springframework.stereotype.Service;
import rooms_reservation.application.model_dto.Estatisticas;
import rooms_reservation.application.model_dto.Reserva;
import rooms_reservation.application.repository.ReservaRepository;
import rooms_reservation.application.service.ReservaService;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.util.*;

@Service
public class ReservaServiceImpl implements ReservaService {

    final ReservaRepository reservaRepository;

    public ReservaServiceImpl(ReservaRepository reservaRepository) {
        this.reservaRepository = reservaRepository;
    }

    @Override
    public void salvar(Reserva reserva) {

        OffsetDateTime inicio = reserva.getInicio();
        OffsetDateTime fim = reserva.getFim();

        LocalTime horaInicio = inicio.toLocalTime();
        LocalTime horaFim = fim.toLocalTime();

        LocalTime abertura = LocalTime.of(8, 0);
        LocalTime fechamento = LocalTime.of(18, 0);


        if(horaInicio.isBefore(abertura) || horaInicio.isAfter(fechamento)) {
            throw new IllegalArgumentException("O horário de início deve estar entre 08:00 e 17:30.");
        }

        if(horaFim.isAfter(fechamento) || horaFim.isBefore(abertura)) {
            throw new IllegalArgumentException("O horário de fim deve estar entre 08:30 e 18:00.");
        }

        if(fim.isBefore(inicio)) {
            throw new IllegalArgumentException("O horário de fim deve ser posterior ao horário de início.");
        }

        Duration duracao = Duration.between(inicio, fim);
        Duration minimoMinutos = Duration.ofMinutes(30);
        Duration maximoMinutos = Duration.ofMinutes(120);
        if(duracao.compareTo(minimoMinutos) < 0 || duracao.compareTo(maximoMinutos) > 0) {
            throw new IllegalArgumentException("A reserva deve durar entre 30 minutos à 2 horas");
        }

        if(inicio.isBefore(OffsetDateTime.now())){
            throw new IllegalArgumentException("As reservas retroativas são proibidas");
        }

        if(! (inicio.getMinute() % 30 == 0 && inicio.getSecond() == 0)){
            throw new IllegalArgumentException("Reservas devem ter início em intervalos de 30 minutos (ex: 10:00, 10:30)");
        }

        reservaRepository.salvar(reserva);
    }

    @Override
    public List<Reserva> listarReservas(){
        return reservaRepository.buscarTodas();
    }

    @Override
    public Estatisticas listarEstatisticasDia(){
        List<Reserva> lista = reservaRepository.buscarData(OffsetDateTime.now().toLocalDate());

        if (lista == null || lista.isEmpty()) {
            return Estatisticas.builder()
                    .countReservas(0)
                    .salasUtilizadas(0)
                    .tempoTotalReservadoMinutos(0L)
                    .mediaDuracaoMinutos(0.0)
                    .maiorDuracaoMinutos(0L)
                    .build();
        }

        Set<String> salasUnicas = new HashSet<>();
        int totalReservas = lista.size();
        int salasEmUso = 0;
        long tempoEmUso = 0;
        double mediaDuracaoMin = 0;
        long maxDuracaoMin = 0;

        for(Reserva reserva : lista){

            long duracaoMinutos = (reserva.getFim().toEpochSecond() - reserva.getInicio().toEpochSecond()) / 60;
            tempoEmUso += duracaoMinutos;

            if(duracaoMinutos >= maxDuracaoMin) { maxDuracaoMin = duracaoMinutos; }
            salasUnicas.add(reserva.getSala().toString());

        }

        salasEmUso = salasUnicas.size();
        mediaDuracaoMin = (double) tempoEmUso / totalReservas;

        return Estatisticas.builder()
                .maiorDuracaoMinutos(maxDuracaoMin)
                .mediaDuracaoMinutos(new BigDecimal(mediaDuracaoMin).setScale(2, RoundingMode.HALF_UP).doubleValue())
                .salasUtilizadas(salasEmUso)
                .countReservas(totalReservas)
                .tempoTotalReservadoMinutos(tempoEmUso).build();
    }

    @Override
    public void limpar(){
        reservaRepository.limpar();
    }

}
