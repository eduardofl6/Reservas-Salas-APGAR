package rooms_reservation.application.repository;

import rooms_reservation.application.model_dto.Reserva;

import java.time.LocalDate;
import java.util.List;

/**
 * Repositório que faz uso da memória local visando concorrência.
  * Utilizado um ConcurrentHashMap particionado por dia (LocalDate) para
 * dar write-lock apenas no bucket específico usando o método compute(), evitando travamentos globais.
 * Para a agenda das salas, a escolha foi o CopyOnWriteArrayList: um trade-off
 * onde assumimos um custo maior de alocação durante a gravação (POST) para garantir
 * leituras (GET) simultâneas rápidas, sem bloqueios e sem risco de ConcurrentModificationException que ocorre em List normais.
 */
public interface ReservaRepository {

    /**
     * Salva uma reserva na memória caso não sobreponha alguma existente.
     *
     * @param newReserva Objeto contendo os dados da reserva.
     */
    void salvar(Reserva newReserva) ;

    /**
     * Retorna todas reservas existentes em uma lista.
     *
     * @return Lista contendo todas reservas existentes.
     */
    List<Reserva> buscarTodas() ;

    /**
     * Retorna lista de reservas se baseando na data.
     *
     * @param date Data de consulta.
     * @return Lista lista contendo todas reservas daquela data.
     */
    List<Reserva> buscarData(LocalDate date);

    /**
     * Remove todas reservas existentes na memória.
     */
    void limpar();
}
