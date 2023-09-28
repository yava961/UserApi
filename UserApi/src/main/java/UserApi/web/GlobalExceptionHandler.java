package UserApi.web;

import UserApi.dto.error.ErrorDto;
import UserApi.exception.NotFoundException;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.Collections;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(NotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ResponseEntity<ErrorDto> handleNotFoundException(NotFoundException e) {
        var errorDto = new ErrorDto(LocalDateTime.now(), Collections.singletonList(e.getMessage()));
        return new ResponseEntity<>(errorDto, HttpStatus.NOT_FOUND);
    }


    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorDto> handleIllegalArgumentException(IllegalArgumentException e) {
        var errorDto = new ErrorDto(LocalDateTime.now(), Collections.singletonList(e.getMessage()));
        return new ResponseEntity<>(errorDto, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorDto> handleInvalidRequestParams(MethodArgumentNotValidException e) {
        var errors = e.getBindingResult().getAllErrors().stream().map(this::convertObjectError).toList();
        return ResponseEntity.badRequest().body(new ErrorDto(LocalDateTime.now(), errors));
    }
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorDto> handleInvalidRequestParams(HttpMessageNotReadableException e) {
        ErrorDto errorDto = null;
        if(e.getCause() != null && e.getCause() instanceof InvalidFormatException){
            errorDto = new ErrorDto(LocalDateTime.now(), Collections.singletonList("Date should be in format YYYY-MM-dd"));

        }else {
            errorDto = new ErrorDto(LocalDateTime.now(), Collections.singletonList(e.getMessage()));
        }
        return new ResponseEntity<>(errorDto, HttpStatus.BAD_REQUEST);
    }

    private String convertObjectError(ObjectError objectError) {
        String fieldName = ((FieldError) objectError).getField();
        String errorMessage = objectError.getDefaultMessage();
        return String.format("Field name %s %s", fieldName, errorMessage);
    }
}
