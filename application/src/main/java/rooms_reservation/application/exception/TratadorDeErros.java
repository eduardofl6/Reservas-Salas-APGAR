package rooms_reservation.application.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import rooms_reservation.application.enums.Sala;
import tools.jackson.databind.exc.InvalidFormatException;


import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class TratadorDeErros {

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, String>> tratarRegraDeNegocio(IllegalArgumentException ex) {
        Map<String, String> resposta = new HashMap<>();
        resposta.put("erro", ex.getMessage());

        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(resposta);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> tratarReservaEntrada(MethodArgumentNotValidException ex) {
        Map<String, String> campos = new HashMap<>();

        for(FieldError fieldError : ex.getBindingResult().getFieldErrors()){
            campos.put(fieldError.getField(),  fieldError.getDefaultMessage());
        }

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(campos);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Map <String, String>> tratarErroDeConversao(HttpMessageNotReadableException ex) {
        Map<String, String> resposta = new HashMap<>();


        if (ex.getCause() instanceof InvalidFormatException) {
            InvalidFormatException cause = (InvalidFormatException) ex.getCause();
            if (cause.getPath() != null && !cause.getPath().isEmpty()) {
                String nomeDoCampo = cause.getPath().get(0).getPropertyName();
                Class<?> tipoAlvo = cause.getTargetType();
                Object valorInformado = cause.getValue();

                if ("sala".equals(nomeDoCampo)) {
                    String mensagem = String.format(
                            "A sala '%s' não existe. As opções válidas são: %s",
                            valorInformado,
                            Arrays.toString(Sala.values())
                    );
                        resposta.put("erro", mensagem);
                        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(resposta);

                }

                if ("fim".equals(nomeDoCampo) || "inicio".equals(nomeDoCampo)){
                    String mensagem = String.format(
                            "O horário de '%s' não está no padrão ISO-8601 (exemplo: 2026-08-15T10:00:00-03:00 ou 2026-03-08T15:00:00-03:00).",
                            valorInformado
                    );
                    resposta.put("erro", mensagem);
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(resposta);
                }

            }
        }



        resposta.put("erro", "O corpo da requisição está mal formatado ou contém valores inválidos.");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(resposta);
    }


}
