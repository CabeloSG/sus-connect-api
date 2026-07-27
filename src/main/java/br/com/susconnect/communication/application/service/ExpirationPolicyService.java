package br.com.susconnect.communication.application.service;

import br.com.susconnect.appointment.domain.entity.Appointment;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

/**
 * Serviço responsável por definir a política de expiração
 * das comunicações enviadas aos pacientes.
 *
 * Atualmente, a comunicação expira dois dias antes da data
 * da consulta. Essa implementação foi isolada para permitir
 * futuras alterações nas regras de negócio sem impactar
 * os casos de uso.
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
public class ExpirationPolicyService {

    /**
     * Calcula a data e hora de expiração da comunicação.
     *
     * Regra atual:
     * Expira dois dias antes da consulta.
     *
     * @param appointment consulta agendada.
     * @return data e hora de expiração.
     */
    public LocalDateTime calculateExpiration(Appointment appointment) {
        return appointment.getConfirmationDeadline();
    }

}