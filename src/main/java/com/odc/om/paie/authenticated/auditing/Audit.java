package com.odc.om.paie.authenticated.auditing;

import java.util.UUID;


import com.odc.om.paie.authenticated.enums.Action;
import com.odc.om.paie.authenticated.user.User;
import com.odc.om.paie.entities.AbstractEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.JoinColumn;

@Entity
public class Audit extends AbstractEntity {
    @Enumerated(EnumType.STRING)
    @Column(length = 50)
    private Action action;

    private String entityName;
    private UUID entityId;

    @ManyToOne
    @JoinColumn(name = "utilisateur_id")
    private User utilisateur;

    public Audit(Action action, String entityName, UUID entityId, User utilisateur) {
        this.action = action;
        this.entityName = entityName;
        this.entityId = entityId;
        this.setUtilisateur(utilisateur); // Associer l'utilisateur qui a effectué l'audit
    }

    public Audit() {

    }

    // Getters et Setters
    public Action getAction() {
        return action;
    }

    public void setAction(Action action) {
        this.action = action;
    }

    public String getEntityName() {
        return entityName;
    }

    public void setEntityName(String entityName) {
        this.entityName = entityName;
    }

    public UUID getEntityId() {
        return entityId;
    }

    public void setEntityId(UUID entityId) {
        this.entityId = entityId;
    }

    public User getUtilisateur() {
        return utilisateur;
    }

    public void setUtilisateur(User utilisateur) {
        this.utilisateur = utilisateur;
    }
}
