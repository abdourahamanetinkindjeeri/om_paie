package com.odc.om.paie.repositories;

import com.odc.om.paie.entities.Transaction;
import com.odc.om.paie.entities.TransactionStatus;
import com.odc.om.paie.entities.TransactionType;
import com.odc.om.paie.entities.Wallet;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, UUID> {

    List<Transaction> findByWallet(Wallet wallet);

    Page<Transaction> findByWallet(Wallet wallet, Pageable pageable);

    List<Transaction> findByWalletAndStatus(Wallet wallet, TransactionStatus status);

    List<Transaction> findByWalletAndType(Wallet wallet, TransactionType type);

    Optional<Transaction> findByReference(String reference);

    @Query("SELECT t FROM Transaction t WHERE t.wallet = :wallet ORDER BY t.creationDate DESC")
    List<Transaction> findByWalletOrderByCreationDateDesc(@Param("wallet") Wallet wallet);

    @Query("SELECT t FROM Transaction t WHERE t.wallet = :wallet ORDER BY t.creationDate DESC")
    Page<Transaction> findByWalletOrderByCreationDateDesc(@Param("wallet") Wallet wallet, Pageable pageable);

    boolean existsByReference(String reference);
}