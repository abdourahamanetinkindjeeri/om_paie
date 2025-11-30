package com.odc.om.paie.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "merchants")
@EqualsAndHashCode(callSuper=false)
public class Merchant extends AbstractEntity {

    @Column(nullable = false)
    private String nom;

    @Column(unique = true, nullable = false)
    private String code;

    @Column(nullable = false)
    private String telephone;

    @Column(unique = true, nullable = false)
    private String email;

    @Builder.Default
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MerchantStatus statut = MerchantStatus.ACTIVE;
}