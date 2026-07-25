package br.com.susconnect.common.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Representa o padrão de resposta das APIs do SUS Connect.
 *
 * Todas as operações da aplicação devem retornar
 * este objeto como padrão de resposta.
 *
 * @author Leandro Gonçalves
 * @author Felipe
 * @author Lucas
 * @since 1.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SuccessResponse<T> {

    /**
     * Indica se a operação foi realizada com sucesso.
     */
    private boolean success;

    /**
     * Mensagem da operação.
     */
    private String message;

    /**
     * Data e hora da resposta.
     */
    @Builder.Default
    private LocalDateTime timestamp = LocalDateTime.now();

    /**
     * Dados retornados pela API.
     */
    private T data;

    /**
     * Cria uma resposta de sucesso.
     *
     * @param message mensagem da operação.
     * @param data dados retornados.
     * @return resposta padronizada.
     */
    public static <T> SuccessResponse<T> success(String message, T data) {

        return SuccessResponse.<T>builder()
                .success(true)
                .message(message)
                .data(data)
                .build();

    }

    /**
     * Cria uma resposta de erro.
     *
     * @param message mensagem de erro.
     * @return resposta padronizada.
     */
    public static <T> SuccessResponse<T> error(String message) {

        return SuccessResponse.<T>builder()
                .success(false)
                .message(message)
                .data(null)
                .build();

    }

}