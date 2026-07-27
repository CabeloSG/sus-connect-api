package br.com.susconnect.ml.infrastructure.client;

import br.com.susconnect.ml.application.dto.NoShowPredictionRequest;
import br.com.susconnect.ml.application.dto.NoShowPredictionResponse;
import br.com.susconnect.ml.domain.exception.MlServiceUnavailableException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

/**
 * Cliente responsável pela comunicação entre
 * o SUS Connect e o serviço de Machine Learning.
 *
 * Envia as features de um agendamento ao serviço
 * Python e recebe a probabilidade prevista de
 * ausência do paciente.
 *
 * Projeto: SUS Connect
 * Hackathon FIAP - Arquitetura e Desenvolvimento Java
 *
 * @author Leandro Gonçalves
 * @author Felipe
 * @author Lucas
 * @since 1.0
 */
@Component
public class NoShowMlClient {

    private final RestClient restClient;

    public NoShowMlClient(
            RestClient.Builder restClientBuilder,
            @Value("${sus-connect.ml.base-url}")
            String baseUrl) {

        this.restClient = restClientBuilder
                .baseUrl(baseUrl)
                .build();
    }

    /**
     * Solicita ao modelo de Machine Learning
     * a predição do risco de ausência.
     *
     * @param request features utilizadas pelo modelo.
     * @return resultado da predição.
     */
    public NoShowPredictionResponse predict(
            NoShowPredictionRequest request) {

        try {

            NoShowPredictionResponse response = restClient
                    .post()
                    .uri("/predict")
                    .body(request)
                    .retrieve()
                    .body(NoShowPredictionResponse.class);

            if (response == null) {
                throw new MlServiceUnavailableException(
                        "O serviço de Machine Learning retornou uma resposta vazia."
                );
            }

            return response;

        } catch (MlServiceUnavailableException exception) {

            throw exception;

        } catch (RestClientException exception) {

            throw new MlServiceUnavailableException(
                    "Não foi possível consultar o serviço de Machine Learning.",
                    exception
            );
        }
    }
}