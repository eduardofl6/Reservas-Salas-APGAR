package rooms_reservation.application.controller;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import rooms_reservation.application.model_dto.Estatisticas;
import rooms_reservation.application.model_dto.Reserva;
import rooms_reservation.application.service.ReservaService;

import java.util.List;

@RestController
public class ReservaController {

    final ReservaService reservaService;

    public ReservaController(ReservaService reservaService){
        this.reservaService = reservaService;
    }

    @PostMapping("/reservas")
    public ResponseEntity<Void> criarReserva(@RequestBody @Valid Reserva reserva){
        reservaService.salvar(reserva);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping("/reservas")
    public ResponseEntity<List<Reserva>> listarReservas(){
        List<Reserva> lista = reservaService.listarReservas();

        return ResponseEntity.status(HttpStatus.OK).body(lista);
    }

    @GetMapping("/estatisticas")
    public ResponseEntity<Estatisticas> listarEstastisticasDia(){
        Estatisticas estatisticas = reservaService.listarEstatisticasDia();

        return ResponseEntity.status(HttpStatus.OK).body(estatisticas);
    }

    @DeleteMapping("/reservas")
    public ResponseEntity<Void> deletarReservas(){
        reservaService.limpar();
        return ResponseEntity.status(HttpStatus.OK).build();
    }
}
