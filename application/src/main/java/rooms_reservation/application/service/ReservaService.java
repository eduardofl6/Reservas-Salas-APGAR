package rooms_reservation.application.service;

import rooms_reservation.application.model_dto.Estatisticas;
import rooms_reservation.application.model_dto.Reserva;

import java.util.List;

/**
 * Interface principal do serviço de reservas.
 * O objetivo aqui é centralizar todas as regras de negócio (horário comercial,
 * duração e sobreposição de horários), isolando a lógica do Controller e do Repositório.
 */
public interface ReservaService {

    /**
     * Valida as regras de negócio e tenta agendar uma nova sala.
     *
     * @param reserva Objeto contendo os dados da reserva. O offset do fuso horário é preservado.
     * @throws IllegalArgumentException caso a sala já esteja ocupada no período solicitado,
     *                                  a duração seja inválida, ou o horário fuja da janela das 08:00 às 18:00.
     */
    void salvar(Reserva reserva);

    /**
     * Busca todas as reservas registradas em memória.
     *
     * @return Lista com todas as reservas.
     */
    List<Reserva> listarReservas();

    /**
     * Varre as reservas do dia atual para calcular as métricas de ocupação.
     *
     * @return Estatísticas consolidadas do dia (retorna zerado caso não haja reservas).
     */
    Estatisticas listarEstatisticasDia();

    /**
     * Esvazia completamente a base de dados em memória.
     */
    void limpar();
}
