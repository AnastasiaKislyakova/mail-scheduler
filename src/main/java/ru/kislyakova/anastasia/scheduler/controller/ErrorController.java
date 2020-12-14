package ru.kislyakova.anastasia.scheduler.controller;

import com.fasterxml.jackson.core.JsonParseException;
import org.springframework.core.codec.DecodingException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.support.WebExchangeBindException;
import org.springframework.web.server.ServerWebInputException;
import reactor.core.publisher.Mono;
import ru.kislyakova.anastasia.scheduler.entity.Error;
import ru.kislyakova.anastasia.scheduler.exception.SchedulerApiException;

import java.util.List;
import java.util.stream.Collectors;

@RestControllerAdvice(basePackages = "ru.kislyakova.anastasia.scheduler.controller")
public class ErrorController {
    @ExceptionHandler(SchedulerApiException.class)
    public Mono<ResponseEntity<Error>> handleException(SchedulerApiException ex) {
        return Mono.subscriberContext().map((it) -> {
            Error error = new Error(ex.getCode(), ex.getReason());
            return ResponseEntity.status(ex.getStatus()).body(error);
        });
    }

    @ExceptionHandler(Exception.class)
    public Mono<ResponseEntity<Error>> handleException(Exception ex) {
        return Mono.subscriberContext().map((it) -> {
            Error error = new Error("Unknown error", ex.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        });
    }

    @ExceptionHandler(ServerWebInputException.class)
    public Mono<ResponseEntity<Error>> handleWebException(ServerWebInputException ex) {
        return Mono.subscriberContext().map((it) -> {

            Throwable cause = ex.getCause();
            if (cause instanceof DecodingException) {
                DecodingException decodingEx = (DecodingException) cause;
                Error error = new Error("JSON decoding error", decodingEx.getMessage());
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
            }

            //Error error = new Error("Bad Request", ex.getReason());
            Error error = new Error("Bad Request", ex.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        });
    }

    @ExceptionHandler(WebExchangeBindException.class)
    public Mono<ResponseEntity<Error>> handleWebException(WebExchangeBindException ex) {
        return Mono.subscriberContext().map((it) -> {
            List<Error> errors = ex.getAllErrors().stream().map((error) -> {
                String message;
                if (error instanceof FieldError) {
                    FieldError fieldError = ((FieldError) error);
                    message = String.format("Field %s invalid: %s", fieldError.getField(), error.getDefaultMessage());
                } else {
                    message = error.getDefaultMessage();
                }
                return new Error("WrongFieldValue", message);
            }).collect(Collectors.toList());
            Error error = new Error("Validation", "Failed to validate input request", errors);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        });
    }
}
