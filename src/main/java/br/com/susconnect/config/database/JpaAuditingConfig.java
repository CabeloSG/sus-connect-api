package br.com.susconnect.config.database;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

/**
 * Habilita a auditoria automática do Spring Data JPA.
 *
 * <p>Responsável pelo preenchimento automático dos campos
 * de criação e atualização das entidades.</p>
 *
 * <p>Projeto: SUS Connect</p>
 *
 * @author Leandro Gonçalves
 * @author Felipe
 * @author Lucas
 * @since 1.0
 */
@Configuration
@EnableJpaAuditing
public class JpaAuditingConfig {
}