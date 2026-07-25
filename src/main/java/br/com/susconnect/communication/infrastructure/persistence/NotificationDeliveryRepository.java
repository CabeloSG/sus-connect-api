package br.com.susconnect.communication.infrastructure.persistence;

import br.com.susconnect.communication.domain.entity.NotificationDelivery;
import br.com.susconnect.communication.domain.enums.DeliveryStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface NotificationDeliveryRepository
        extends JpaRepository<NotificationDelivery, UUID> {

    Optional<NotificationDelivery> findByToken(String token);

    List<NotificationDelivery> findByCommunicationId(UUID communicationId);

    List<NotificationDelivery> findByStatus(DeliveryStatus status);

}