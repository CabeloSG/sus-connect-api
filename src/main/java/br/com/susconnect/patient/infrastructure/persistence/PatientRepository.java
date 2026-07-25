package br.com.susconnect.patient.infrastructure.persistence;

import br.com.susconnect.patient.domain.entity.Patient;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

/**
 * Repositório responsável pelo acesso aos dados de pacientes.
 *
 * Projeto: SUS Connect
 * Hackathon FIAP
 *
 * @author Leandro Gonçalves
 * @author Felipe
 * @author Lucas
 * @since 1.0
 */
public interface PatientRepository extends JpaRepository<Patient, UUID> {

    Optional<Patient> findByCpf(String cpf);

    boolean existsByCpf(String cpf);

    boolean existsBySusCard(String susCard);

}