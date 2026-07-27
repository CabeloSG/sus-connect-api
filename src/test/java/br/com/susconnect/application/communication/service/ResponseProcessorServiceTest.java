package br.com.susconnect.application.communication.service;

import br.com.susconnect.appointment.domain.entity.Appointment;
import br.com.susconnect.appointment.domain.enums.AppointmentStatus;
import br.com.susconnect.appointment.domain.enums.AppointmentType;
import br.com.susconnect.appointment.domain.enums.MedicalSpecialty;
import br.com.susconnect.appointment.infrastructure.persistence.AppointmentRepository;
import br.com.susconnect.availability.application.dto.NextAvailabilityResponse;
import br.com.susconnect.availability.application.event.NextAvailabilityFoundEvent;
import br.com.susconnect.availability.application.usecase.FindNextAvailabilityUseCase;
import br.com.susconnect.availability.application.usecase.ReleaseAvailableSlotUseCase;
import br.com.susconnect.availability.infrastructure.messaging.NextAvailabilityEventPublisher;
import br.com.susconnect.common.exception.ResourceNotFoundException;
import br.com.susconnect.communication.application.command.dto.RegisterPatientResponseRequest;
import br.com.susconnect.communication.application.service.ResponseProcessorService;
import br.com.susconnect.communication.application.validator.CommunicationResponseValidator;
import br.com.susconnect.communication.domain.entity.Communication;
import br.com.susconnect.communication.domain.entity.NotificationDelivery;
import br.com.susconnect.communication.domain.enums.CommunicationStatus;
import br.com.susconnect.communication.domain.enums.DeliveryStatus;
import br.com.susconnect.communication.domain.enums.NotificationChannel;
import br.com.susconnect.communication.domain.enums.PatientResponse;
import br.com.susconnect.communication.infrastructure.persistence.CommunicationRepository;
import br.com.susconnect.communication.infrastructure.persistence.NotificationDeliveryRepository;
import br.com.susconnect.patient.domain.entity.Patient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Testes unitários do serviço responsável pelo
 * processamento da resposta do paciente.
 *
 * Projeto: SUS Connect
 * Hackathon FIAP - Arquitetura e Desenvolvimento Java
 *
 * @author Leandro Gonçalves
 * @author Felipe
 * @author Lucas
 * @since 1.0
 */
@ExtendWith(MockitoExtension.class)
class ResponseProcessorServiceTest {

    @Mock
    private NotificationDeliveryRepository notificationDeliveryRepository;

    @Mock
    private CommunicationRepository communicationRepository;

    @Mock
    private CommunicationResponseValidator communicationResponseValidator;

    @Mock
    private AppointmentRepository appointmentRepository;

    @Mock
    private ReleaseAvailableSlotUseCase releaseAvailableSlotUseCase;

    @Mock
    private FindNextAvailabilityUseCase findNextAvailabilityUseCase;

    @Mock
    private NextAvailabilityEventPublisher nextAvailabilityEventPublisher;

    @InjectMocks
    private ResponseProcessorService service;

    private UUID patientId;
    private UUID appointmentId;
    private UUID communicationId;
    private UUID currentDeliveryId;

    private Patient patient;
    private Appointment appointment;
    private Communication communication;

    private NotificationDelivery currentDelivery;
    private NotificationDelivery smsDelivery;
    private NotificationDelivery emailDelivery;

    @BeforeEach
    void setUp() {

        patientId = UUID.randomUUID();
        appointmentId = UUID.randomUUID();
        communicationId = UUID.randomUUID();
        currentDeliveryId = UUID.randomUUID();

        patient = Patient.builder()
                .fullName("João da Silva")
                .build();

        patient.setId(patientId);

        appointment = Appointment.builder()
                .patient(patient)
                .appointmentDateTime(
                        LocalDateTime.of(2026, 8, 20, 9, 0)
                )
                .appointmentType(AppointmentType.CONSULTATION)
                .medicalSpecialty(MedicalSpecialty.CLINICO_GERAL)
                .doctor("Dr. Carlos Silva")
                .healthUnit("UBS Central")
                .status(AppointmentStatus.PENDING_CONFIRMATION)
                .confirmationDeadline(
                        LocalDateTime.of(2026, 8, 18, 9, 0)
                )
                .build();

        appointment.setId(appointmentId);

        communication = Communication.builder()
                .appointment(appointment)
                .status(CommunicationStatus.PENDING)
                .expirationDate(
                        LocalDateTime.of(2026, 8, 18, 9, 0)
                )
                .build();

        communication.setId(communicationId);

        currentDelivery = NotificationDelivery.builder()
                .communication(communication)
                .channel(NotificationChannel.WHATSAPP)
                .token("token-whatsapp")
                .status(DeliveryStatus.SENT)
                .build();

        currentDelivery.setId(currentDeliveryId);

        smsDelivery = NotificationDelivery.builder()
                .communication(communication)
                .channel(NotificationChannel.SMS)
                .token("token-sms")
                .status(DeliveryStatus.SENT)
                .build();

        smsDelivery.setId(UUID.randomUUID());

        emailDelivery = NotificationDelivery.builder()
                .communication(communication)
                .channel(NotificationChannel.EMAIL)
                .token("token-email")
                .status(DeliveryStatus.SENT)
                .build();

        emailDelivery.setId(UUID.randomUUID());
    }

    @Test
    void shouldConfirmAppointmentWhenPatientRespondsYes() {

        RegisterPatientResponseRequest request =
                createRequest(PatientResponse.YES);

        when(notificationDeliveryRepository.findByToken(
                request.getToken()))
                .thenReturn(Optional.of(currentDelivery));

        when(notificationDeliveryRepository.findByCommunicationId(
                communicationId))
                .thenReturn(List.of(
                        currentDelivery,
                        smsDelivery,
                        emailDelivery
                ));

        service.process(request);

        assertEquals(
                PatientResponse.YES,
                currentDelivery.getPatientResponse()
        );

        assertEquals(
                DeliveryStatus.RESPONDED,
                currentDelivery.getStatus()
        );

        assertEquals(
                CommunicationStatus.CONFIRMED,
                communication.getStatus()
        );

        assertEquals(
                AppointmentStatus.CONFIRMED,
                appointment.getStatus()
        );

        assertEquals(
                DeliveryStatus.INVALIDATED,
                smsDelivery.getStatus()
        );

        assertEquals(
                DeliveryStatus.INVALIDATED,
                emailDelivery.getStatus()
        );

        verify(communicationResponseValidator)
                .validateStatus(currentDelivery);

        verify(communicationResponseValidator)
                .validateCommunicationExpiration(currentDelivery);

        verify(notificationDeliveryRepository)
                .save(currentDelivery);

        verify(communicationRepository)
                .save(communication);

        verify(appointmentRepository)
                .save(appointment);

        verify(releaseAvailableSlotUseCase, never())
                .execute(any(Appointment.class));

        verify(findNextAvailabilityUseCase, never())
                .execute(any(Appointment.class));

        verify(nextAvailabilityEventPublisher, never())
                .publish(any(NextAvailabilityFoundEvent.class));

        verify(notificationDeliveryRepository)
                .saveAll(any());
    }

    @Test
    void shouldCancelAppointmentReleaseSlotAndPublishNextAvailabilityWhenPatientRespondsNo() {

        RegisterPatientResponseRequest request =
                createRequest(PatientResponse.NO);

        UUID availableSlotId = UUID.randomUUID();

        LocalDateTime nextDate =
                LocalDateTime.of(2026, 8, 25, 10, 0);

        NextAvailabilityResponse nextAvailability =
                new NextAvailabilityResponse(
                        availableSlotId,
                        nextDate,
                        AppointmentType.CONSULTATION,
                        MedicalSpecialty.CLINICO_GERAL,
                        "Dra. Maria Souza",
                        "UBS Norte"
                );

        when(notificationDeliveryRepository.findByToken(
                request.getToken()))
                .thenReturn(Optional.of(currentDelivery));

        when(findNextAvailabilityUseCase.execute(appointment))
                .thenReturn(Optional.of(nextAvailability));

        when(notificationDeliveryRepository.findByCommunicationId(
                communicationId))
                .thenReturn(List.of(
                        currentDelivery,
                        smsDelivery,
                        emailDelivery
                ));

        service.process(request);

        assertEquals(
                PatientResponse.NO,
                currentDelivery.getPatientResponse()
        );

        assertEquals(
                DeliveryStatus.RESPONDED,
                currentDelivery.getStatus()
        );

        assertEquals(
                CommunicationStatus.DECLINED,
                communication.getStatus()
        );

        assertEquals(
                AppointmentStatus.CANCELLED,
                appointment.getStatus()
        );

        assertEquals(
                DeliveryStatus.INVALIDATED,
                smsDelivery.getStatus()
        );

        assertEquals(
                DeliveryStatus.INVALIDATED,
                emailDelivery.getStatus()
        );

        verify(releaseAvailableSlotUseCase)
                .execute(appointment);

        verify(findNextAvailabilityUseCase)
                .execute(appointment);

        ArgumentCaptor<NextAvailabilityFoundEvent> eventCaptor =
                ArgumentCaptor.forClass(
                        NextAvailabilityFoundEvent.class
                );

        verify(nextAvailabilityEventPublisher)
                .publish(eventCaptor.capture());

        NextAvailabilityFoundEvent event =
                eventCaptor.getValue();

        assertEquals(patientId, event.patientId());
        assertEquals(appointmentId, event.cancelledAppointmentId());
        assertEquals(availableSlotId, event.availableSlotId());
        assertEquals(nextDate, event.appointmentDateTime());
        assertEquals(
                AppointmentType.CONSULTATION,
                event.appointmentType()
        );
        assertEquals(
                MedicalSpecialty.CLINICO_GERAL,
                event.medicalSpecialty()
        );
        assertEquals(
                "Dra. Maria Souza",
                event.doctor()
        );
        assertEquals(
                "UBS Norte",
                event.healthUnit()
        );
    }

    @Test
    void shouldCancelAppointmentWithoutPublishingEventWhenNextAvailabilityDoesNotExist() {

        RegisterPatientResponseRequest request =
                createRequest(PatientResponse.NO);

        when(notificationDeliveryRepository.findByToken(
                request.getToken()))
                .thenReturn(Optional.of(currentDelivery));

        when(findNextAvailabilityUseCase.execute(appointment))
                .thenReturn(Optional.empty());

        when(notificationDeliveryRepository.findByCommunicationId(
                communicationId))
                .thenReturn(List.of(
                        currentDelivery,
                        smsDelivery,
                        emailDelivery
                ));

        service.process(request);

        assertEquals(
                CommunicationStatus.DECLINED,
                communication.getStatus()
        );

        assertEquals(
                AppointmentStatus.CANCELLED,
                appointment.getStatus()
        );

        verify(appointmentRepository)
                .save(appointment);

        verify(releaseAvailableSlotUseCase)
                .execute(appointment);

        verify(findNextAvailabilityUseCase)
                .execute(appointment);

        verify(nextAvailabilityEventPublisher, never())
                .publish(any(NextAvailabilityFoundEvent.class));

        verify(notificationDeliveryRepository)
                .saveAll(any());
    }

    @Test
    void shouldThrowResourceNotFoundExceptionWhenTokenDoesNotExist() {

        RegisterPatientResponseRequest request =
                createRequest(PatientResponse.YES);

        when(notificationDeliveryRepository.findByToken(
                request.getToken()))
                .thenReturn(Optional.empty());

        ResourceNotFoundException exception =
                assertThrows(
                        ResourceNotFoundException.class,
                        () -> service.process(request)
                );

        assertEquals(
                "Entrega da notificação não encontrada para o token informado.",
                exception.getMessage()
        );

        verify(notificationDeliveryRepository)
                .findByToken(request.getToken());

        verify(communicationResponseValidator, never())
                .validateStatus(any());

        verify(communicationResponseValidator, never())
                .validateCommunicationExpiration(any());

        verify(notificationDeliveryRepository, never())
                .save(any(NotificationDelivery.class));

        verify(communicationRepository, never())
                .save(any(Communication.class));

        verify(appointmentRepository, never())
                .save(any(Appointment.class));

        verify(releaseAvailableSlotUseCase, never())
                .execute(any(Appointment.class));

        verify(findNextAvailabilityUseCase, never())
                .execute(any(Appointment.class));

        verify(nextAvailabilityEventPublisher, never())
                .publish(any(NextAvailabilityFoundEvent.class));
    }

    private RegisterPatientResponseRequest createRequest(
            PatientResponse response) {

        RegisterPatientResponseRequest request =
                new RegisterPatientResponseRequest();

        request.setToken("token-whatsapp");
        request.setResponse(response);

        return request;
    }
}