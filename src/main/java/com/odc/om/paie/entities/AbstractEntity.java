package com.odc.om.paie.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.odc.om.paie.authenticated.user.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
@ToString
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public abstract class AbstractEntity implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID) 
    @Column(name = "id", updatable = false, nullable = false, columnDefinition = "uuid")
    private UUID id;

    @CreatedDate
    @Column(name = "creation_date", nullable = false, updatable = false)
    @JsonIgnore
    private Instant creationDate;

    @LastModifiedDate
    @Column(name = "last_modified_date", nullable = false)
    @JsonIgnore
    private Instant lastModifiedDate;

    @CreatedBy
    @ManyToOne(fetch = FetchType.LAZY) // ✅ Lazy pour éviter les fuites de données
    @JoinColumn(name = "created_by_id", referencedColumnName = "id")
    private User utilisateur;

    @LastModifiedBy
    @ManyToOne(fetch = FetchType.LAZY) // ✅ Lazy pour limiter l'exposition
    @JoinColumn(name = "last_modified_by_id", referencedColumnName = "id")
    private User utilisateurLastModifiedBy;
}
