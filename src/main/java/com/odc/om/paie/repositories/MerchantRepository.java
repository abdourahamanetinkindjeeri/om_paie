package com.odc.om.paie.repositories;

import com.odc.om.paie.entities.Merchant;
import com.odc.om.paie.entities.MerchantStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface MerchantRepository extends JpaRepository<Merchant, UUID> {

    Optional<Merchant> findByCode(String code);

    Optional<Merchant> findByEmail(String email);

    List<Merchant> findByStatut(MerchantStatus statut);

    boolean existsByCode(String code);

    boolean existsByEmail(String email);
}