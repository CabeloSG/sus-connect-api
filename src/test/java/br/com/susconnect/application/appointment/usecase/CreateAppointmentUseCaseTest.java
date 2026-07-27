package br.com.susconnect.application.appointment.usecase;

import br.com.susconnect.appointment.application.dto.AppointmentResponse;
import br.com.susconnect.appointment.application.dto.CreateAppointmentRequest;
import br.com.susconnect.appointment.application.mapper.AppointmentMapper;
import br.com.susconnect.appointment.application.usecase.CreateAppointmentUseCase;
import br.com.susconnect.appointment.domain.entity.Appointment;
import br.com.susconnect.appointment.domain.enums.AppointmentStatus;
import br.com.susconnect.appointment.domain.enums.AppointmentType;
import br.com.susconnect.appointment.domain.enums.MedicalSpecialty;
import br.com.susconnect.appointment.infrastructure.persistence.AppointmentRepository;
import br.com.susconnect.common.exception.BusinessException;
import br.com.susconnect.common.exception.ResourceNotFoundException;
import br.com.susconnect.communication.application.command.usecase.GenerateCommunicationUseCase;
import br.com.susconnect.patient.domain.entity.Patient;
import br.com.susconnect.patient.domain.enums.PatientStatus;
import br.com.susconnect.patient.infrastructure.persistence.PatientRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Testes unitários do caso de uso responsável
 * pela criação de agendamentos.
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
class CreateAppointmentUseCaseTest {

    @Mock
    private AppointmentRepository appointmentRepository;

    @Mock
    private PatientRepository patientRepository;

    @Mock
    private AppointmentMapper mapper;

    @Mock
    private GenerateCommunicationUseCase generateCommunicationUseCase;

    @InjectMocks
    private CreateAppointmentUseCase useCase;

    private UUID patientId;
    private UUID appointmentId;

    private CreateAppointmentRequest request;
    private Patient patient;
    private Appointment appointment;
    private AppointmentResponse response;

    @BeforeEach
    void setUp() {

        patientId = UUID.randomUUID();
        appointmentId = UUID.randomUUID();

        LocalDateTime appointmentDateTime =
                LocalDateTime.of(2026, 8, 20, 9, 0);

        request = CreateAppointmentRequest.builder()
                .patientId(patientId)
                .appointmentDateTime(appointmentDateTime)
                .appointmentType(AppointmentType.CONSULTATION)
                .medicalSpecialty(MedicalSpecialty.CLINICO_GERAL)
                .doctor("Dr. Carlos Silva")
                .healthUnit("UBS Central")
                .build();

        patient = Patient.builder()
                .fullName("João da Silva")
                .cpf("12345678901")
                .birthDate(LocalDate.of(1995, 8, 12))
                .phone("67999998888")
                .email("joao@email.com")
                .susCard("706123456789000")
                .status(PatientStatus.ACTIVE)
                .build();

        /*
         * Como o ID pertence à BaseEntity e possui setter,
         * simulamos aqui um paciente já persistido.
         */
        patient.setId(patientId);

        appointment = Appointment.builder()
                .appointmentDateTime(appointmentDateTime)
                .appointmentType(AppointmentType.CONSULTATION)
                .medicalSpecialty(MedicalSpecialty.CLINICO_GERAL)
                .doctor("Dr. Carlos Silva")
                .healthUnit("UBS Central")
                .status(AppointmentStatus.PENDING_CONFIRMATION)
                .confirmationDeadline(appointmentDateTime.minusDays(2))
                .build();

        /*
         * Simula o resultado do save() realizado pelo repository.
         */
        appointment.setId(appointmentId);

        response = AppointmentResponse.builder()
                .id(appointmentId)
                .patientId(patientId)
                .appointmentDateTime(appointmentDateTime)
                .appointmentType(AppointmentType.CONSULTATION)
                .medicalSpecialty(MedicalSpecialty.CLINICO_GERAL)
                .doctor("Dr. Carlos Silva")
                .healthUnit("UBS Central")
                .status(AppointmentStatus.PENDING_CONFIRMATION)
                .confirmationDeadline(appointmentDateTime.minusDays(2))
                .build();
    }

    @Test
    void shouldCreateAppointmentSuccessfully() {

        when(appointmentRepository.existsByDoctorAndAppointmentDateTime(
                request.getDoctor(),
                request.getAppointmentDateTime()))
                .thenReturn(false);

        when(patientRepository.findById(patientId))
                .thenReturn(Optional.of(patient));

        when(mapper.toEntity(request))
                .thenReturn(appointment);

        when(appointmentRepository.save(appointment))
                .thenReturn(appointment);

        when(mapper.toResponse(appointment))
                .thenReturn(response);

        AppointmentResponse result = useCase.execute(request);

        assertEquals(appointmentId, result.getId());
        assertEquals(patientId, result.getPatientId());
        assertEquals(
                AppointmentStatus.PENDING_CONFIRMATION,
                result.getStatus()
        );

        /*
         * Valida uma regra importante do use case:
         * o paciente encontrado deve ser associado ao agendamento.
         */
        assertSame(patient, appointment.getPatient());

        verify(appointmentRepository)
                .existsByDoctorAndAppointmentDateTime(
                        request.getDoctor(),
                        request.getAppointmentDateTime()
                );

        verify(patientRepository).findById(patientId);
        verify(mapper).toEntity(request);
        verify(appointmentRepository).save(appointment);

        /*
         * Regra crítica:
         * após salvar, deve iniciar o fluxo de comunicação.
         */
        verify(generateCommunicationUseCase)
                .execute(appointmentId);

        verify(mapper).toResponse(appointment);
    }

    @Test
    void shouldThrowBusinessExceptionWhenDoctorAlreadyHasAppointmentAtSameTime() {

        when(appointmentRepository.existsByDoctorAndAppointmentDateTime(
                request.getDoctor(),
                request.getAppointmentDateTime()))
                .thenReturn(true);

        BusinessException exception = assertThrows(
                BusinessException.class,
                () -> useCase.execute(request)
        );

        assertEquals(
                "Já existe um agendamento para este médico nesse horário.",
                exception.getMessage()
        );

        verify(appointmentRepository)
                .existsByDoctorAndAppointmentDateTime(
                        request.getDoctor(),
                        request.getAppointmentDateTime()
                );

        verify(patientRepository, never())
                .findById(any(UUID.class));

        verify(mapper, never())
                .toEntity(any(CreateAppointmentRequest.class));

        verify(appointmentRepository, never())
                .save(any(Appointment.class));

        verify(generateCommunicationUseCase, never())
                .execute(any(UUID.class));
    }

    @Test
    void shouldThrowResourceNotFoundExceptionWhenPatientDoesNotExist() {

        when(appointmentRepository.existsByDoctorAndAppointmentDateTime(
                request.getDoctor(),
                request.getAppointmentDateTime()))
                .thenReturn(false);

        when(patientRepository.findById(patientId))
                .thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> useCase.execute(request)
        );

        assertEquals(
                "Paciente não encontrado.",
                exception.getMessage()
        );

        verify(appointmentRepository)
                .existsByDoctorAndAppointmentDateTime(
                        request.getDoctor(),
                        request.getAppointmentDateTime()
                );

        verify(patientRepository).findById(patientId);

        verify(mapper, never())
                .toEntity(any(CreateAppointmentRequest.class));

        verify(appointmentRepository, never())
                .save(any(Appointment.class));

        verify(generateCommunicationUseCase, never())
                .execute(any(UUID.class));
    }
}