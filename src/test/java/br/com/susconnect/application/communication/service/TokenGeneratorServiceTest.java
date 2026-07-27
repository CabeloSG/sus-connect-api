package br.com.susconnect.application.communication.service;

import br.com.susconnect.communication.application.service.TokenGeneratorService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class TokenGeneratorServiceTest {

    private TokenGeneratorService tokenGeneratorService;

    @BeforeEach
    void setUp() {
        tokenGeneratorService = new TokenGeneratorService();
    }

    @Test
    void shouldGenerateValidUuidToken() {

        String token = tokenGeneratorService.generate();

        assertNotNull(token);
        assertFalse(token.isBlank());

        assertDoesNotThrow(() ->
                UUID.fromString(token)
        );
    }

    @Test
    void shouldGenerateDifferentTokens() {

        String firstToken =
                tokenGeneratorService.generate();

        String secondToken =
                tokenGeneratorService.generate();

        assertNotEquals(
                firstToken,
                secondToken
        );
    }
}