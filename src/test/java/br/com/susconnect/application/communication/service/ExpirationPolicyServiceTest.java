package br.com.susconnect.application.communication.service;

import br.com.susconnect.appointment.domain.entity.Appointment;
import br.com.susconnect.communication.application.service.ExpirationPolicyService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ExpirationPolicyServiceTest {

    private ExpirationPolicyService expirationPolicyService;

    @BeforeEach
    void setUp() {
        expirationPolicyService =
                new ExpirationPolicyService();
    }

    @Test
    void shouldReturnAppointmentConfirmationDeadline() {

        LocalDateTime confirmationDeadline =
                LocalDateTime.of(
                        2026,
                        8,
                        10,
                        18,
                        0
                );

        Appointment appointment =
                Appointment.builder()
                        .confirmationDeadline(
                                confirmationDeadline
                        )
                        .build();

        LocalDateTime expiration =
                expirationPolicyService
                        .calculateExpiration(appointment);

        assertEquals(
                confirmationDeadline,
                expiration
        );
    }
}