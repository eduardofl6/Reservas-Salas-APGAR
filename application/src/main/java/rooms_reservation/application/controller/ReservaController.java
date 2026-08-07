package rooms_reservation.application.controller;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import rooms_reservation.application.dto.Reserva;
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

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping("/estatisticas")
    public ResponseEntity<List<Reserva>> listarEstastisticasDia(){

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @DeleteMapping("/reservas")
    public ResponseEntity<Void> deletarReservas(){

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}
