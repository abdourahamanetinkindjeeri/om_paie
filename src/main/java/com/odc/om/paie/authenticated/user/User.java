package com.odc.om.paie.authenticated.user;



import com.odc.om.paie.authenticated.token.Token;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.Collections;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "app_user")
public class User implements UserDetails {
    @Builder.Default
    private int failedAttempts = 0;

    @Id
    @GeneratedValue
    private UUID id;
    private String firstname;
    private String lastname;
    @Column(unique = true)
    private String email;

    private String telephone;

    private String pin;

    private String otpCode;
    private LocalDateTime otpExpiry;

    private String typePiece;
    private String numero;
    private String adresse;
    private String photo;

    @OneToMany(mappedBy = "user")
    private List<Token> tokens;



    // Code a ajoute
    @Builder.Default
    @Column(nullable = false)
    private boolean active = false;

    @Builder.Default
    private boolean isBlocked = false;


    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.emptyList();
    }

    @Override
    public String getPassword() {
        return null;
    }

    @Override
        public String getUsername() {
            return telephone;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return !isBlocked;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
