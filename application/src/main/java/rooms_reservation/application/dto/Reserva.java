package rooms_reservation.application.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.Builder;
import rooms_reservation.application.enums.Sala;

import java.time.OffsetDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class Reserva {

    @NotNull(message = "Campo sala vazio ou zerado")
    private Sala sala;

    @NotBlank(message = "Campo responsavel vazio")
    private String responsavel;

    @NotNull(message = "Campo inicio vazio ou zerado")
    private OffsetDateTime inicio;

    @NotNull(message = "Campo fim vazio ou zerado")
    private OffsetDateTime fim;

}
