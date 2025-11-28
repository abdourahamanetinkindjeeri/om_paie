package com.odc.om.paie.authenticated.auditing;


import com.odc.om.paie.authenticated.enums.Action;
import com.odc.om.paie.entities.AbstractEntity;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreRemove;
import jakarta.persistence.PreUpdate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class EntityAuditListener {

    private static final Logger logger = LoggerFactory.getLogger(EntityAuditListener.class);
    private final AuditRepository auditRepository;

    @Autowired
    public EntityAuditListener(AuditRepository auditRepository) {
        this.auditRepository = auditRepository;
    }

    @PrePersist
    @PreUpdate
    @PreRemove
    public void logAudit(AbstractEntity entity) {
        Action action = (entity.getId() == null) ? Action.CREATE : Action.UPDATE;

        logger.debug("Début de l'audit pour l'entité: {}", entity.getClass().getSimpleName()); // Log initial

        if (entity.getId() == null) {
            action = Action.CREATE;
        } else if (entity.getId() != null) {
            action = Action.UPDATE;
        }

        // Créer un audit à partir de l'entité
        Audit audit = new Audit(action, entity.getClass().getSimpleName(), entity.getId(), entity.getUtilisateur());
        auditRepository.save(audit); // Persister l'audit

        logger.debug("Audit enregistré avec succès");  // Log final
    }
}
