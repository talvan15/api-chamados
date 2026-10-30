package br.edu.ifrn.chamados.api;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.*;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.http.converter.HttpMessageNotReadableException;

import java.time.Instant;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(ApiException.class)
    ResponseEntity<ErroApi> api(ApiException e, HttpServletRequest r) {
        return response(e.getStatus(), e.getMessage(), r);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    ResponseEntity<ErroApi> valid(MethodArgumentNotValidException e, HttpServletRequest r) {
        String msg = e.getBindingResult().getFieldErrors().stream().map(x -> x.getField() + ": " + x.getDefaultMessage()).findFirst().orElse("Dados inválidos");
        return response(HttpStatus.BAD_REQUEST, msg, r);
    }

    @ExceptionHandler({MethodArgumentTypeMismatchException.class, IllegalArgumentException.class})
    ResponseEntity<ErroApi> bad(Exception e, HttpServletRequest r) {
        return response(HttpStatus.BAD_REQUEST, "Parâmetro ou valor inválido: " + e.getMessage(), r);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    ResponseEntity<ErroApi> unreadable(HttpMessageNotReadableException e, HttpServletRequest r) {
        return response(HttpStatus.BAD_REQUEST, "Corpo JSON inválido ou contém valor incompatível com o tipo esperado.", r);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    ResponseEntity<ErroApi> constraint(DataIntegrityViolationException e, HttpServletRequest r) {
        return response(HttpStatus.CONFLICT, "A operação viola uma restrição de integridade ou um valor único.", r);
    }

    @ExceptionHandler(Exception.class)
    ResponseEntity<ErroApi> internal(Exception e, HttpServletRequest r) {
        return response(HttpStatus.INTERNAL_SERVER_ERROR, "Erro interno ao processar a solicitação.", r);
    }

    private ResponseEntity<ErroApi> response(HttpStatus s, String m, HttpServletRequest r) {
        return ResponseEntity.status(s).body(new ErroApi(Instant.now(), s.value(), m, r.getRequestURI()));
    }
}
