package br.com.susconnect.confirmation.infrastructure.persistence;

import br.com.susconnect.confirmation.domain.entity.Confirmation;
import br.com.susconnect.confirmation.domain.enums.ConfirmationStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

/**
 * Repositório responsável pelo acesso aos dados das confirmações.
 *
 * Projeto: SUS Connect
 * Hackathon FIAP
 *
 * @author Leandro Gonçalves
 * @author Felipe
 * @author Lucas
 * @since 1.0
 */
public interface ConfirmationRepository
        extends JpaRepository<Confirmation, UUID> {

    Optional<Confirmation> findByToken(String token);

    boolean existsByToken(String token);

    Optional<Confirmation> findByAppointmentId(UUID appointmentId);

    long countByStatus(ConfirmationStatus status);

}