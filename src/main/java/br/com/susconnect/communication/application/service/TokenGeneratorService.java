package br.com.susconnect.communication.application.service;

import org.springframework.stereotype.Service;

import java.util.UUID;

/**
 * Serviço responsável pela geração de tokens únicos utilizados
 * nas notificações enviadas aos pacientes.
 *
 * Atualmente utiliza UUID, porém sua implementação foi isolada
 * para permitir futuras evoluções, como utilização de JWT,
 * tokens criptografados ou integrações externas, sem impactar
 * as regras de negócio.
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
public class TokenGeneratorService {

    /**
     * Gera um token único.
     *
     * @return token único.
     */
    public String generate() {
        return UUID.randomUUID().toString();
    }

}