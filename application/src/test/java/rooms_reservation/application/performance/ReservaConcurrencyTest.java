package rooms_reservation.application.performance;


import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import rooms_reservation.application.enums.Sala;
import rooms_reservation.application.model_dto.Reserva;
import rooms_reservation.application.service.ReservaService;

import java.time.OffsetDateTime;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@Disabled("Desabilitado pois stress-tests não devem estar habilitados em contexto CI/CD")
//Possível também marcar com Tag("stress") e executa-los em um job diferente de maneira assíncrona para fazer uso do test.
class ReservaConcurrencyTest {

    @Autowired
    private ReservaService reservaService;

    @Test
    @DisplayName("Deve suportar 1000 requisições simultâneas de leitura e escrita")
    void testeDeEstresseEConcorrencia() throws InterruptedException {
        int numeroDeThreads = 50;
        int totalDeRequisicoes = 1000;

        ExecutorService executor = Executors.newFixedThreadPool(numeroDeThreads);
        CountDownLatch latch = new CountDownLatch(totalDeRequisicoes);

        AtomicInteger erros = new AtomicInteger(0);

        reservaService.limpar();

        OffsetDateTime dataBase = OffsetDateTime.now().plusDays(1).withHour(8).withMinute(0).withSecond(0).withNano(0);
        long startTime = System.currentTimeMillis();

        for (int i = 0; i < totalDeRequisicoes; i++) {
            final int index = i;
            executor.submit(() -> {
                try {
                    if (index % 50 == 0) {
                        int slot = index / 50;

                        OffsetDateTime inicioValido = dataBase.plusMinutes(slot * 30L);
                        OffsetDateTime fimValido = inicioValido.plusMinutes(30);

                        Reserva reserva = Reserva.builder()
                                .sala(Sala.A101)
                                .responsavel("Thread User " + index)
                                .inicio(inicioValido)
                                .fim(fimValido)
                                .build();
                        reservaService.salvar(reserva);
                    } else {
                        reservaService.listarReservas();
                    }
                } catch (Exception e) {
                    erros.incrementAndGet();
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();
        executor.shutdown();

        long endTime = System.currentTimeMillis();
        System.out.println("Tempo total para 1000 requisições concorrentes: " + (endTime - startTime) + "ms");

        assertEquals(0, erros.get(), "Ocorreram erros de concorrência (ex: ConcurrentModificationException)");
        assertEquals(20, reservaService.listarReservas().size(), "Exatamente 100 escritas deveriam ter sido computadas");
    }
}

