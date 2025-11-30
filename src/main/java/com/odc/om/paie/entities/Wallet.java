package com.odc.om.paie.entities;

import com.odc.om.paie.authenticated.user.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "wallets", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"user_id", "is_main"}),
    @UniqueConstraint(columnNames = {"merchant_id", "is_main"})
})
@EqualsAndHashCode(callSuper=false)
public class Wallet extends AbstractEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "merchant_id")
    private Merchant merchant;

    @Builder.Default
    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal balance = BigDecimal.ZERO;

    @Builder.Default
    @Column(nullable = false, length = 3)
    private String currency = "XOF";

    @Builder.Default
    @Column(name = "is_main", nullable = false)
    private Boolean isMain = false;

    // Helper methods
    public boolean belongsToUser() {
        return user != null;
    }

    public boolean belongsToMerchant() {
        return merchant != null;
    }

    public String getOwnerId() {
        if (belongsToUser()) {
            return user.getId().toString();
        } else if (belongsToMerchant()) {
            return merchant.getId().toString();
        }
        return null;
    }
}