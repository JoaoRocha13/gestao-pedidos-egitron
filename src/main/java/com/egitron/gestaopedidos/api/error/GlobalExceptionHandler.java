package com.egitron.gestaopedidos.api.error;

import com.egitron.gestaopedidos.exception.BadRequestException;
import com.egitron.gestaopedidos.exception.NotFoundException;
import com.egitron.gestaopedidos.service.ErrorLogService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import javax.servlet.http.HttpServletRequest;

/**
 * Handler global de exceções da API.
 * Transforma exceções em respostas JSON (ApiError).
 * Todos os erros são guardados em BD via ErrorLogService.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    private final ErrorLogService errorLogService;

    public GlobalExceptionHandler(ErrorLogService errorLogService) {
        this.errorLogService = errorLogService;
    }

    // 400 - erro de validação ou regra de negócio
    @ExceptionHandler({
            BadRequestException.class,
            MethodArgumentNotValidException.class,
            MethodArgumentTypeMismatchException.class
    })
    public ResponseEntity<ApiError> handleBadRequest(Exception ex, HttpServletRequest req) {
        // guarda no error_log
        errorLogService.log(
                ex.getMessage(),
                ex,
                req.getMethod() + " " + req.getRequestURI()
        );

        ApiError body = new ApiError(
                400,
                "Bad Request",
                cleanMessage(ex),
                req.getRequestURI()
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }

    // 404 - recurso não encontrado
    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ApiError> handleNotFound(NotFoundException ex, HttpServletRequest req) {
        // guarda no error_log
        errorLogService.log(
                ex.getMessage(),
                ex,
                req.getMethod() + " " + req.getRequestURI()
        );

        ApiError body = new ApiError(
                404,
                "Not Found",
                cleanMessage(ex),
                req.getRequestURI()
        );
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(body);
    }

    // 500 - erro inesperado
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handleUnexpected(Exception ex, HttpServletRequest req) {
        // guarda no error_log
        errorLogService.log(
                ex.getMessage(),
                ex,
                req.getMethod() + " " + req.getRequestURI()
        );

        ApiError body = new ApiError(
                500,
                "Internal Server Error",
                "Unexpected error. Please try again later.",
                req.getRequestURI()
        );
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(body);
    }

    private String cleanMessage(Exception ex) {
        String m = ex.getMessage();
        if (m == null || m.trim().isEmpty()) {
            return ex.getClass().getSimpleName();
        }
        return m;
    }
}
