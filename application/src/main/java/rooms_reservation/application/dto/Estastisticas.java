package rooms_reservation.application.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class Estastisticas {

    private int countReservas;

    private int salasUtilizadas;

    private long tempoTotalReservadoMinutos;

    private double mediaDuracaoMinutos;

    private double maiorDuracaoMinutos;

}
