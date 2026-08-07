package rooms_reservation.application.service.impl;

import org.springframework.stereotype.Service;
import rooms_reservation.application.model_dto.Reserva;
import rooms_reservation.application.repository.ReservaRepository;
import rooms_reservation.application.service.ReservaService;

import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.util.List;

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
            throw new IllegalArgumentException("O horário de início deve estar entre 08:00 e 18:00.");
        }

        if(horaFim.isAfter(fechamento) || horaFim.isBefore(abertura)) {
            throw new IllegalArgumentException("O horário de fim deve estar entre 08:00 e 18:00. ");
        }

        if(fim.isBefore(inicio)) {
            throw new IllegalArgumentException("O horário de fim deve ser posterior ao horário de início.");
        }

        long duracaoMinutos = (fim.toEpochSecond() - inicio.toEpochSecond()) / 60;
        if((duracaoMinutos < 30 || duracaoMinutos > 120)){
            throw new IllegalArgumentException("A reserva deve durar entre 30 minutos à 2 horas");
        }

        if(inicio.isBefore(OffsetDateTime.now())){
            throw new IllegalArgumentException("A reserva retroativas são proibidas");
        }

        if(! (inicio.getMinute() % 30 == 0 && inicio.getSecond() == 0)){
            throw new IllegalArgumentException("Reservas devem ter início em intervalos de 30 minutos (ex: 10:00, 10:30)");
        }
//        long inicioEmMinutos = (inicio.toEpochSecond() / 60);
//        if(! (inicioEmMinutos % 30 == 0)){
//        Abortado para facilitar legibilidade.

        reservaRepository.salvar(reserva);
    }

    @Override
    public List<Reserva> listarReservas(){
        return reservaRepository.buscarTodas();
    }

    @Override
    public void limpar(){

    }

}
