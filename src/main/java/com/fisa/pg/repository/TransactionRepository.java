package com.fisa.pg.repository;

import com.fisa.pg.entity.transaction.Transaction;
import com.fisa.pg.entity.user.Merchant;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TransactionRepository extends JpaRepository<Transaction, String> {
    List<Transaction> findByMerchant(Merchant merchant);
}
