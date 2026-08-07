package rooms_reservation.application.repository;

import rooms_reservation.application.model_dto.Reserva;

import java.time.LocalDate;
import java.util.List;

public interface ReservaRepository {
    public void salvar(Reserva newReserva) ;

    public List<Reserva> buscarTodas() ;

    public List<Reserva> buscarData(LocalDate date);

    public void limpar();
}
