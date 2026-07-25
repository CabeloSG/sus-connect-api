package br.com.susconnect.common.exception;

/**
 * Exceção utilizada para representar regras de negócio.
 *
 * @author Leandro Gonçalves
 * @author Felipe
 * @author Lucas
 * @since 1.0
 */
public class BusinessException extends RuntimeException {

    public BusinessException(String message) {
        super(message);
    }

}