package rooms_reservation.application.repository.impl;

import org.springframework.stereotype.Repository;
import rooms_reservation.application.dto.Reserva;
import rooms_reservation.application.repository.ReservaRepository;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

@Repository
public class ReservaRepositoryImpl implements ReservaRepository {

    final ConcurrentHashMap<LocalDate, List<Reserva>> reservas = new ConcurrentHashMap<>();

    @Override
    public void salvar(Reserva newReserva) {
        LocalDate date = newReserva.getInicio().toLocalDate();

        reservas.compute(date, (key, newListReservas) ->{
            if (newListReservas == null) {
                newListReservas = new CopyOnWriteArrayList<>();
            }

            for(Reserva r :  newListReservas) {
                if(r.getSala().equals(newReserva.getSala())) {
                    if(newReserva.getInicio().isBefore(r.getFim()) && newReserva.getFim().isAfter(r.getInicio())){
                        throw new IllegalArgumentException("Horário Indisponível! Cômodo reservado para:" +
                                r.toString());
                    }
                }
            }

            newListReservas.add(newReserva);
            return newListReservas;
        });

    }

    @Override
    public List<Reserva> buscarTodas() {
        List<Reserva> listaUnica = new ArrayList<>();

        for (List<Reserva> listaDoDia : reservas.values()) {
            listaUnica.addAll(listaDoDia);
        }

        return listaUnica;
    }

    @Override
    public List<Reserva> buscarData(LocalDate date) {
        return reservas.getOrDefault(date, Collections.emptyList());
    }

    @Override
    public void limpar() {
        reservas.clear();
    }
}