package rooms_reservation.application.repository;

import rooms_reservation.application.dto.Reserva;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public interface ReservaRepository {
    public void salvar(Reserva newReserva) ;

    public List<Reserva> buscarTodas() ;

    public List<Reserva> buscarData(LocalDate date);

    public void limpar();
}
