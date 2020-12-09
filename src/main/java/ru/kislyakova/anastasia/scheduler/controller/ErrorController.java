package ru.kislyakova.anastasia.scheduler.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import reactor.core.publisher.Mono;
import ru.kislyakova.anastasia.scheduler.entity.Error;
import ru.kislyakova.anastasia.scheduler.exception.SchedulerApiException;

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

}
