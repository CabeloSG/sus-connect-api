package br.com.susconnect.application.communication.usecase;

import br.com.susconnect.communication.application.command.dto.RegisterPatientResponseRequest;
import br.com.susconnect.communication.application.command.usecase.RegisterPatientResponseUseCase;
import br.com.susconnect.communication.application.service.ResponseProcessorService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

/**
 * Testes unitários do caso de uso responsável
 * pelo registro da resposta do paciente.
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
class RegisterPatientResponseUseCaseTest {

    @Mock
    private ResponseProcessorService responseProcessorService;

    @InjectMocks
    private RegisterPatientResponseUseCase useCase;

    @Test
    void shouldDelegatePatientResponseProcessing() {

        RegisterPatientResponseRequest request =
                new RegisterPatientResponseRequest();

        useCase.execute(request);

        verify(responseProcessorService)
                .process(request);

        verifyNoMoreInteractions(responseProcessorService);
    }
}