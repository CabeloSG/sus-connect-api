package br.com.susconnect.common.entity;

import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Classe base para todas as entidades do sistema.
 *
 * Centraliza atributos comuns como:
 * - Identificador UUID
 * - Data de criação
 * - Data de atualização
 *
 * Todas as entidades do domínio devem herdar desta classe.
 *
 * @author Leandro Gonçalves
 * @author Felipe
 * @author Lucas
 * @since 1.0
 */
@Getter
@Setter
@MappedSuperclass
public abstract class BaseEntity {

    /**
     * Identificador único da entidade.
     */
    @Id
    @GeneratedValue
    private UUID id;

    /**
     * Data e hora de criação do registro.
     */
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /**
     * Data e hora da última atualização.
     */
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    /**
     * Executado automaticamente antes da inserção
     * de um novo registro no banco de dados.
     */
    @PrePersist
    protected void onCreate() {
        LocalDateTime now = LocalDateTime.now();
        createdAt = now;
        updatedAt = now;
    }

    /**
     * Executado automaticamente antes da atualização
     * de um registro existente.
     */
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

}