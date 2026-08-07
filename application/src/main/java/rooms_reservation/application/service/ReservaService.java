package rooms_reservation.application.service;

import rooms_reservation.application.dto.Reserva;

public interface ReservaService {

    public void salvar(Reserva reserva);

    public void limpar();
}
