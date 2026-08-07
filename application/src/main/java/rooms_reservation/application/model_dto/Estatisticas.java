package rooms_reservation.application.model_dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class Estatisticas {

    private int countReservas;

    private int salasUtilizadas;

    private long tempoTotalReservadoMinutos;

    private double mediaDuracaoMinutos;

    private long maiorDuracaoMinutos;

}
