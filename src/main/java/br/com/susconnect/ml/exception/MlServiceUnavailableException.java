package br.com.susconnect.ml.domain.exception;

/**
 * Exceção lançada quando o serviço de Machine Learning
 * do SUS Connect não está disponível ou não consegue
 * processar uma predição.
 *
 * Projeto: SUS Connect
 * Hackathon FIAP - Arquitetura e Desenvolvimento Java
 *
 * @author Leandro Gonçalves
 * @author Felipe
 * @author Lucas
 * @since 1.0
 */
public class MlServiceUnavailableException extends RuntimeException {

    public MlServiceUnavailableException(String message) {
        super(message);
    }

    public MlServiceUnavailableException(
            String message,
            Throwable cause) {

        super(message, cause);
    }
}