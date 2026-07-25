package br.com.susconnect.communication.application.command.usecase;

import br.com.susconnect.communication.application.command.dto.RegisterPatientResponseRequest;
import br.com.susconnect.communication.application.service.ResponseProcessorService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Caso de uso responsável por registrar a resposta do paciente
 * referente a uma comunicação enviada pelo SUS Connect.
 *
 * Este caso de uso apenas orquestra o fluxo da operação,
 * delegando todas as regras de negócio ao
 * {@link ResponseProcessorService}.
 *
 * Projeto: SUS Connect
 * Hackathon FIAP - Arquitetura e Desenvolvimento Java
 *
 * @author Leandro Gonçalves
 * @author Felipe
 * @author Lucas
 * @since 1.0
 */
@Service
@RequiredArgsConstructor
@Transactional
public class RegisterPatientResponseUseCase {

    private final ResponseProcessorService responseProcessorService;

    /**
     * Executa o fluxo de processamento da resposta enviada
     * pelo paciente.
     *
     * @param request dados da resposta do paciente.
     */
    public void execute(RegisterPatientResponseRequest request) {

        responseProcessorService.process(request);

    }

}