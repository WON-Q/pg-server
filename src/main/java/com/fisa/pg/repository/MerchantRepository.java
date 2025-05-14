package com.fisa.pg.repository;

import com.fisa.pg.entity.user.Merchant;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MerchantRepository extends JpaRepository<Merchant, Long> {
}
