package com.fisa.pg.repository;

import com.fisa.pg.entity.card.UserCard;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserCardRepository extends JpaRepository<UserCard, Long> {

}
