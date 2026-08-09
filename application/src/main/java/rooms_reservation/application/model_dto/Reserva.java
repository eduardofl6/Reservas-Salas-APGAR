package rooms_reservation.application.model_dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.Builder;
import rooms_reservation.application.enums.Sala;

import java.time.OffsetDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Reserva {

    @NotNull(message = "Campo sala vazio ou zerado")
    private Sala sala;

    @NotBlank(message = "Campo responsavel vazio")
    private String responsavel;

    @NotNull(message = "Campo inicio vazio ou zerado")
    @JsonFormat(without = JsonFormat.Feature.ADJUST_DATES_TO_CONTEXT_TIME_ZONE)
    private OffsetDateTime inicio;

    @NotNull(message = "Campo fim vazio ou zerado")
    @JsonFormat(without = JsonFormat.Feature.ADJUST_DATES_TO_CONTEXT_TIME_ZONE)
    private OffsetDateTime fim;

}
