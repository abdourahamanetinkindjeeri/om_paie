package com.odc.om.paie.repositories;

import com.odc.om.paie.authenticated.user.User;
import com.odc.om.paie.entities.Merchant;
import com.odc.om.paie.entities.Wallet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface WalletRepository extends JpaRepository<Wallet, UUID> {

    List<Wallet> findByUser(User user);

    List<Wallet> findByMerchant(Merchant merchant);

    Optional<Wallet> findByUserAndIsMain(User user, Boolean isMain);

    Optional<Wallet> findByMerchantAndIsMain(Merchant merchant, Boolean isMain);

    @Query("SELECT w FROM Wallet w WHERE w.user = :user AND w.isMain = true")
    Optional<Wallet> findMainWalletByUser(@Param("user") User user);

    @Query("SELECT w FROM Wallet w WHERE w.merchant = :merchant AND w.isMain = true")
    Optional<Wallet> findMainWalletByMerchant(@Param("merchant") Merchant merchant);

    @Query("SELECT w FROM Wallet w WHERE w.user = :user AND w.isMain = false")
    List<Wallet> findSecondaryWalletsByUser(@Param("user") User user);

    @Query("SELECT w FROM Wallet w WHERE w.merchant = :merchant AND w.isMain = false")
    List<Wallet> findSecondaryWalletsByMerchant(@Param("merchant") Merchant merchant);

    boolean existsByUserAndIsMain(User user, Boolean isMain);

    boolean existsByMerchantAndIsMain(Merchant merchant, Boolean isMain);
}