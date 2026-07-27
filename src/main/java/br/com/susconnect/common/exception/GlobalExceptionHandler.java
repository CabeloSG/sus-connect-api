package br.com.susconnect.common.exception;

import br.com.susconnect.common.response.ErrorResponse;
import br.com.susconnect.ml.domain.exception.MlServiceUnavailableException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.stream.Collectors;

/**
 * Handler global responsável por interceptar todas as exceções
 * lançadas pela aplicação e convertê-las em respostas HTTP
 * padronizadas.
 *
 * Todas as APIs do SUS Connect devem utilizar este mecanismo
 * para garantir consistência nas respostas de erro.
 *
 * Projeto: SUS Connect
 * Hackathon FIAP - Arquitetura e Desenvolvimento Java
 *
 * @author Leandro Gonçalves
 * @author Felipe
 * @author Lucas
 * @since 1.0
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Trata exceções de recurso não encontrado.
     *
     * @param ex exceção lançada.
     * @param request requisição HTTP.
     * @return resposta HTTP 404.
     */
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleResourceNotFoundException(
            ResourceNotFoundException ex,
            HttpServletRequest request) {

        return buildErrorResponse(
                HttpStatus.NOT_FOUND,
                ex.getMessage(),
                request
        );
    }

    /**
     * Trata exceções de regra de negócio.
     *
     * @param ex exceção lançada.
     * @param request requisição HTTP.
     * @return resposta HTTP 400.
     */
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ErrorResponse> handleBusinessException(
            BusinessException ex,
            HttpServletRequest request) {

        return buildErrorResponse(
                HttpStatus.BAD_REQUEST,
                ex.getMessage(),
                request
        );
    }

    /**
     * Trata exceções de validação personalizadas.
     *
     * @param ex exceção lançada.
     * @param request requisição HTTP.
     * @return resposta HTTP 400.
     */
    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(
            ValidationException ex,
            HttpServletRequest request) {

        return buildErrorResponse(
                HttpStatus.BAD_REQUEST,
                ex.getMessage(),
                request
        );
    }

    /**
     * Trata erros de validação gerados pelo Bean Validation
     * (@Valid).
     *
     * @param ex exceção lançada.
     * @param request requisição HTTP.
     * @return resposta HTTP 400.
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleMethodArgumentNotValidException(
            MethodArgumentNotValidException ex,
            HttpServletRequest request) {

        String message = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.joining(", "));

        return buildErrorResponse(
                HttpStatus.BAD_REQUEST,
                message,
                request
        );
    }

    /**
     * Trata indisponibilidade do serviço de Machine Learning.
     *
     * Esse cenário pode ocorrer quando o serviço Python responsável
     * pelas predições estiver indisponível, retornar uma resposta
     * inválida ou ocorrer uma falha durante a comunicação.
     *
     * @param ex exceção lançada pelo cliente de Machine Learning.
     * @param request requisição HTTP.
     * @return resposta HTTP 503.
     */
    @ExceptionHandler(MlServiceUnavailableException.class)
    public ResponseEntity<ErrorResponse> handleMlServiceUnavailableException(
            MlServiceUnavailableException ex,
            HttpServletRequest request) {

        return buildErrorResponse(
                HttpStatus.SERVICE_UNAVAILABLE,
                ex.getMessage(),
                request
        );
    }

    /**
     * Trata qualquer exceção inesperada da aplicação.
     *
     * @param ex exceção lançada.
     * @param request requisição HTTP.
     * @return resposta HTTP 500.
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleException(
            Exception ex,
            HttpServletRequest request) {

        return buildErrorResponse(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "Ocorreu um erro interno na aplicação.",
                request
        );
    }

    /**
     * Constrói uma resposta padronizada para erros.
     *
     * @param status código HTTP.
     * @param message mensagem do erro.
     * @param request requisição HTTP.
     * @return resposta padronizada.
     */
    private ResponseEntity<ErrorResponse> buildErrorResponse(
            HttpStatus status,
            String message,
            HttpServletRequest request) {

        ErrorResponse errorResponse = ErrorResponse.builder()
                .status(status.value())
                .error(status.getReasonPhrase())
                .message(message)
                .path(request.getRequestURI())
                .build();

        return ResponseEntity
                .status(status)
                .body(errorResponse);
    }

}