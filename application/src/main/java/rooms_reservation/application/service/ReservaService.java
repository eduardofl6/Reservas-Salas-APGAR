package rooms_reservation.application.service;

import rooms_reservation.application.model_dto.Reserva;

import java.util.List;

public interface ReservaService {

    public void salvar(Reserva reserva);

    public List<Reserva> listarReservas();

    public void limpar();
}
