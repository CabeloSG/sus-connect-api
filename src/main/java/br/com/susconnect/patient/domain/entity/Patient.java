package br.com.susconnect.patient.domain.entity;

import br.com.susconnect.common.entity.BaseEntity;
import br.com.susconnect.patient.domain.enums.PatientStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

/**
 * Entidade responsável por representar um paciente
 * cadastrado no SUS Connect.
 *
 * Esta entidade contém apenas regras relacionadas ao
 * domínio do paciente.
 *
 * @author Leandro Gonçalves
 * @author Felipe
 * @author Lucas
 * @since 1.0
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor

@Entity
@Table(name = "patients")
public class Patient extends BaseEntity {

    /**
     * Nome completo do paciente.
     */
    @Column(nullable = false, length = 150)
    private String fullName;

    /**
     * CPF do paciente.
     */
    @Column(nullable = false, unique = true, length = 11)
    private String cpf;

    /**
     * Data de nascimento.
     */
    @Column(nullable = false)
    private LocalDate birthDate;

    /**
     * Telefone para contato.
     */
    @Column(nullable = false, length = 20)
    private String phone;

    /**
     * E-mail do paciente.
     */
    @Column(length = 150)
    private String email;

    /**
     * Número do Cartão Nacional de Saúde.
     */
    @Column(nullable = false, unique = true, length = 20)
    private String susCard;

    /**
     * Situação do paciente.
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PatientStatus status;

}