package br.com.susconnect.application.ml.infrastructure;

import br.com.susconnect.ml.application.dto.NoShowPredictionRequest;
import br.com.susconnect.ml.application.dto.NoShowPredictionResponse;
import br.com.susconnect.ml.domain.exception.MlServiceUnavailableException;
import br.com.susconnect.ml.infrastructure.client.NoShowMlClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestClient;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.springframework.test.web.client.ExpectedCount.once;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withServerError;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

import org.springframework.http.HttpMethod;

/**
 * Testes do cliente responsável pela comunicação
 * com o serviço de Machine Learning.
 *
 * Projeto: SUS Connect
 * Hackathon FIAP - Arquitetura e Desenvolvimento Java
 *
 * @author Leandro Gonçalves
 * @author Felipe
 * @author Lucas
 * @since 1.0
 */
class NoShowMlClientTest {

    private MockRestServiceServer server;

    private NoShowMlClient client;

    private NoShowPredictionRequest request;

    @BeforeEach
    void setUp() {

        RestClient.Builder builder =
                RestClient.builder();

        server = MockRestServiceServer
                .bindTo(builder)
                .build();

        client = new NoShowMlClient(
                builder,
                "http://localhost:8000"
        );

        request = new NoShowPredictionRequest(
                36,
                4,
                14,
                10,
                "CONSULTATION",
                "CLINICO_GERAL",
                4,
                1,
                3,
                0.25,
                "PENDING"
        );
    }

    @Test
    void shouldReturnPredictionWhenMlServiceRespondsSuccessfully() {

        server.expect(
                        once(),
                        requestTo("http://localhost:8000/predict")
                )
                .andExpect(method(HttpMethod.POST))
                .andRespond(
                        withSuccess(
                                """
                                {
                                  "noShowProbability": 0.8873,
                                  "riskLevel": "HIGH"
                                }
                                """,
                                MediaType.APPLICATION_JSON
                        )
                );

        NoShowPredictionResponse response =
                client.predict(request);

        assertNotNull(response);

        assertEquals(
                0.8873,
                response.noShowProbability(),
                0.0001
        );

        assertEquals(
                "HIGH",
                response.riskLevel()
        );

        server.verify();
    }

    @Test
    void shouldThrowExceptionWhenMlServiceReturnsEmptyBody() {

        server.expect(
                        once(),
                        requestTo("http://localhost:8000/predict")
                )
                .andExpect(method(HttpMethod.POST))
                .andRespond(
                        withSuccess(
                                "",
                                MediaType.APPLICATION_JSON
                        )
                );

        MlServiceUnavailableException exception =
                assertThrows(
                        MlServiceUnavailableException.class,
                        () -> client.predict(request)
                );

        assertEquals(
                "O serviço de Machine Learning retornou uma resposta vazia.",
                exception.getMessage()
        );

        server.verify();
    }

    @Test
    void shouldThrowExceptionWhenMlServiceIsUnavailable() {

        server.expect(
                        once(),
                        requestTo("http://localhost:8000/predict")
                )
                .andExpect(method(HttpMethod.POST))
                .andRespond(withServerError());

        MlServiceUnavailableException exception =
                assertThrows(
                        MlServiceUnavailableException.class,
                        () -> client.predict(request)
                );

        assertEquals(
                "Não foi possível consultar o serviço de Machine Learning.",
                exception.getMessage()
        );

        assertNotNull(exception.getCause());

        server.verify();
    }
}