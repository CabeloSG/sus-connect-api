package br.com.susconnect.common.exception;

/**
 * Exceção lançada quando ocorre erro de validação.
 *
 * @author Leandro Gonçalves
 * @author Felipe
 * @author Lucas
 * @since 1.0
 */
public class ValidationException extends RuntimeException {

    public ValidationException(String message) {
        super(message);
    }

}