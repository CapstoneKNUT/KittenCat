package org.zerock.b01.controller.advice;

import lombok.extern.log4j.Log4j2;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.validation.ConstraintViolationException;
import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;

@RestControllerAdvice
@Log4j2
public class CustomRestAdvice {

    @ExceptionHandler(BindException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<Map<String, String>> handleBindException(BindException e) {
        log.error("BindException occurred", e);

        Map<String, String> errorMap = new HashMap<>();
        if (e.hasErrors()) {
            BindingResult bindingResult = e.getBindingResult();
            bindingResult.getFieldErrors().forEach(fieldError -> {
                errorMap.put(fieldError.getField(), fieldError.getDefaultMessage());
            });
        }
        return ResponseEntity.badRequest().body(errorMap);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<Map<String, String>> handleDataIntegrityViolationException(Exception e) {
        log.error("DataIntegrityViolationException occurred", e);

        Map<String, String> errorMap = new HashMap<>();
        errorMap.put("time", "" + System.currentTimeMillis());
        errorMap.put("msg", "Data integrity violation");
        return ResponseEntity.badRequest().body(errorMap);
    }

    @ExceptionHandler({ NoSuchElementException.class, EmptyResultDataAccessException.class })
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ResponseEntity<Map<String, String>> handleNoSuchElement(Exception e) {
        log.error("NoSuchElementException or EmptyResultDataAccessException occurred", e);

        Map<String, String> errorMap = new HashMap<>();
        errorMap.put("time", "" + System.currentTimeMillis());
        errorMap.put("msg", "Resource not found");
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorMap);
    }
}
