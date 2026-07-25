package br.com.susconnect.common.response;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * Representa o padrão de resposta para erros da API.
 *
 * <p>Projeto: SUS Connect</p>
 *
 * @author Leandro Gonçalves
 * @author Felipe
 * @author Lucas
 * @since 1.0
 */
@Data
@Builder
public class ErrorResponse {

    @Builder.Default
    private boolean success = false;

    /**
     * Código HTTP retornado.
     */
    private int status;

    /**
     * Tipo do erro.
     */
    private String error;

    /**
     * Mensagem detalhada.
     */
    private String message;

    /**
     * Caminho da requisição.
     */
    private String path;

    /**
     * Data e hora do erro.
     */
    @Builder.Default
    private LocalDateTime timestamp = LocalDateTime.now();

}